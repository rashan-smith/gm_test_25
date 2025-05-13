import { Component, OnInit } from '@angular/core';
import { LoadingService } from '../../services/loading.service';
import { Router } from '@angular/router';
import { SettingsService } from 'src/app/services/settings.service';

@Component({
  selector: 'app-register-school-page',
  templateUrl: './register-school-page.component.html',
  styleUrls: ['./register-school-page.component.scss'],
})
export class RegisterSchoolPageComponent implements OnInit {
  privacyUrl1 = "https://opendatacommons.org/licenses/odbl/1-0/";
  privacyUrl2 = "https://www.measurementlab.net/privacy/";
  targetUrl = "_blank"
  isPrivacyChecked = false;
  constructor(public loading: LoadingService,
    private readonly router: Router,
    private settingsService: SettingsService
  ) { }

  ngOnInit() { }

  toggleCheckbox() {
    this.isPrivacyChecked = !this.isPrivacyChecked;
  }

  redirectToCountry() {
    const loadingMsg =
      // eslint-disable-next-line max-len
      `<div class="loadContent"><ion-img src="assets/loader/country_loader.gif" class="loaderGif"></ion-img><p class="green_loader" [translate]="'searchCountry.check'">Detecting your country</p></div>`;
    this.loading.present(loadingMsg, 3000, 'pdcaLoaderClass', 'null');
    this.router.navigate(['/searchcountry']);

  }

  openExternalUrl(href) {
    this.settingsService.getShell().shell.openExternal(href);
  }

  onContainerClick(event: Event): void {
    const target = event.target as HTMLElement;
    // Do not toggle if clicking directly on the input
    if (target.tagName.toLowerCase() !== 'input') {
      this.isPrivacyChecked = !this.isPrivacyChecked;
    }
  }


}
