import { Component } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../app/services/storage.service';
import { SettingsService } from './services/settings.service';
import { SharedService } from './services/shared-service.service';
import { HistoryService } from './services/history.service';
import { ScheduleService } from './services/schedule.service';
import { environment } from '../environments/environment'; // './esrc/environments/environment';

// const shell = require('electron').shell;
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  languages = environment.languages;
  filteredLanguages = [];
  languageSearch = '';
  school: any;
  historyState: any;
  availableSettings: any;
  scheduleSemaphore: any;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  app_version: any;
  appName = environment.appName;
  showAboutMenu = environment.showAboutMenu;
  testOptions: string[] = ['Ping', 'Download', 'Upload', 'Latency'];
  searchTerm: string = '';
  filteredOptions: string[] = [];
  showDropdown: boolean = false;
  networks = [
    { name: 'WIFI 1', ssid: 'SSID', checked: false },
    { name: 'WIFI 2', ssid: 'SSID', checked: false },
    { name: 'LAN', ssid: 'SSID', checked: false },
  ];
  networkSelected = false
  constructor(
    private menu: MenuController,
    private storage: StorageService,
    public translate: TranslateService,
    private sharedService: SharedService,
    private historyService: HistoryService,
    private settingsService: SettingsService,
    private scheduleService: ScheduleService
  ) {
    this.filteredOptions = [];

    translate.setDefaultLang('en');
    const appLang = this.settingsService.get('applicationLanguage') ?? {
      code: 'en',
    };
    this.translate.use(appLang.code);
    this.app_version = environment.app_version;
    if (this.storage.get('schoolId')) {
      this.school = JSON.parse(this.storage.get('schoolInfo'));
    }
    this.sharedService.on(
      'settings:changed',
      (nameValue: { name: string; value: { code: string } }) => {
        if (nameValue.name === 'applicationLanguage') {
          translate.use(nameValue.value.code);
        }
      }
    );

    this.settingsService.setSetting(
      'scheduledTesting',
      this.settingsService.currentSettings.scheduledTesting
    );
    this.settingsService.setSetting(
      'scheduleInterval',
      this.settingsService.currentSettings.scheduleInterval
    );
    this.availableSettings = this.settingsService.availableSettings;
    if (this.settingsService.currentSettings.scheduledTesting) {
      this.refreshSchedule();
    }
    this.sharedService.on('semaphore:refresh', this.refreshSchedule.bind(this));

    this.sharedService.on(
      'history:measurement:change',
      this.refreshHistory.bind(this)
    );
    this.historyService.reset();
    this.refreshHistory();
    setInterval(() => {
      this.scheduleService.initiate();
    }, 60000);
  }

  openSecond() {
    if (this.storage.get('schoolId')) {
      this.school = JSON.parse(this.storage.get('schoolInfo'));
    }
    this.menu.enable(true, 'second');
    this.menu.open('second');
  }

  openThird() {
    this.menu.enable(true, 'third');
    this.menu.open('third');
  }

  openFourth() {
    this.menu.enable(true, 'fourth');
    this.menu.open('fourth');
  }

  closeMenu() {
    this.menu.enable(true, 'setting');
    this.menu.close();
  }

  

  backMenu() {
    this.closeMenu();
    this.menu.enable(true, 'setting');
    this.menu.open('setting');
  }
  cleanHistory() {
    this.historyService.set({});
  }

  filterLanguages(event: any) {
    const langSearched = event.target.value.toLowerCase();
    this.languageSearch = event.target.value;
    this.filteredLanguages = this.languages.filter(option =>
      option.label.toLowerCase().includes(langSearched)
    );
  }

  selectLanguageDropdown(option: any) {
    this.languageSearch = option.label;
    console.log(this.searchTerm)
    this.filteredLanguages = [];
    this.settingsService.setSetting(
      'applicationLanguage',
      this.languages.find((l) => l.code === option.code)
    );
    // this.selectedLanguageName = this.languages.find(
    //   (l) => l.code === option.code
    // ).label;
    window.location.reload();
    // Hide the dropdown
  }
  filterOptions(event: any) {
    console.log('here')
    const term = event.target.value.toLowerCase();
    // Filter based on user input
    this.searchTerm = event.target.value;
    console.log(this.searchTerm)
    this.filteredOptions = this.testOptions.filter(option =>
      option.toLowerCase().includes(term)
    );
    console.log('hhhih', this.filteredOptions.length, this.searchTerm.length, term.length)

    // Show dropdown if there's at least one match and user has typed something
    this.showDropdown = this.filteredOptions.length > 0 && this.searchTerm.length > 0;
    console.log('thi', this.showDropdown)
  }

  selectOption(option: string) {
    // Set the input to the selected option
    this.searchTerm = option;
    console.log(this.searchTerm)
    this.filteredOptions = [];
    // Hide the dropdown
    this.showDropdown = false;
  }
  refreshHistory() {
    const data = this.historyService.get();
    const dataConsumed = data.measurements.reduce(
      (p: any, c: { results: { [x: string]: any } }) =>
        p + c.results.receivedBytes,
      0
    );
    this.historyState = { dataConsumed };
  }

  onCheckboxChange() {
    // Check if at least one network is selected
    this.networkSelected = this.networks.some(network => network.checked);
  }

  confirmSelection() {
    // Handle the confirm logic
    // e.g., call an API, or navigate to another page
    console.log('Selection confirmed:', this.networks.filter(n => n.checked));
  }


  refreshSchedule() {
    this.scheduleSemaphore = this.scheduleService.getSemaphore();
  }

  openExternalUrl(href) {
    this.settingsService.getShell().shell.openExternal(href);
  }

  closeHelpenu() {
    this.menu.enable(true, 'help');
    this.menu.close();
  }
  backHelpMenu() {
    this.closeMenu();
    this.menu.enable(true, 'help');
    this.menu.open('help');
  }
  openHelpMenu(menuid: string) {
    this.menu.enable(true, menuid);
    this.menu.open(menuid);
  }
}
