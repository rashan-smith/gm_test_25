import { Component, OnInit } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../app/services/storage.service';
import { SettingsService } from './services/settings.service';
import { SharedService } from './services/shared-service.service';
import { HistoryService } from './services/history.service';
import { ScheduleService } from './services/schedule.service';
import { environment } from '../environments/environment'; // './esrc/environments/environment';
import { App } from '@capacitor/app';

// const shell = require('electron').shell;
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {
  school: any;
  historyState: any;
  availableSettings: any;
  scheduleSemaphore: any;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  app_version: any;
  appName = environment.appName;
  showAboutMenu = environment.showAboutMenu;
  constructor(
    private menu: MenuController,
    private storage: StorageService,
    public translate: TranslateService,
    private sharedService: SharedService,
    private historyService: HistoryService,
    private settingsService: SettingsService,
    private scheduleService: ScheduleService
  ) {
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
    this.refreshHistory();
    this.setupEventListener();
  }

  private setupEventListener() {
    console.log('[SpeedTest] Setting up event listeners');

    // Add app state change listener
    App.addListener('appStateChange', ({ isActive }) => {
      console.log('[SpeedTest] App state changed. Is active:', isActive);
      if (isActive) {
        console.log('[SpeedTest] App came to foreground');
      } else {
        console.log('[SpeedTest] App went to background');
      }
    });

    // Add event listener for our background service
    window.addEventListener('timeCheck', (event: any) => {
      console.log('[SpeedTest] Received timeCheck event:', event);

      try {
        // Parse the event data
        const eventData = event.detail ? JSON.parse(event.detail) : null;
        console.log('[SpeedTest] Parsed event data:', eventData);

        // Call your service method
        this.scheduleService.initiate();
        console.log('[SpeedTest] Called scheduleService.initiate()');
      } catch (error) {
        console.error('[SpeedTest] Error handling timeCheck event:', error);
      }
    });

    // Start the background service
    if ((window as any).SpeedTest) {
      console.log('[SpeedTest] Starting background service');
      (window as any).SpeedTest.startBackgroundService()
        .then(() =>
          console.log('[SpeedTest] Background service started successfully')
        )
        .catch((error: any) =>
          console.error('[SpeedTest] Error starting background service:', error)
        );
    } else {
      console.error('[SpeedTest] SpeedTest plugin not found!');
    }
  }

  ngOnInit() {
    // Your existing initialization code
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
    this.menu.enable(true, 'first');
    this.menu.close();
  }

  backMenu() {
    this.closeMenu();
    this.menu.enable(true, 'first');
    this.menu.open('first');
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

  refreshSchedule() {
    this.scheduleSemaphore = this.scheduleService.getSemaphore();
  }

  openExternalUrl(href) {
    this.settingsService.getShell().shell.openExternal(href);
  }
}
