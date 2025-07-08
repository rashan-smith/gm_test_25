import { Component, OnInit, ViewChild } from '@angular/core';
import { LoadingService } from '../../services/loading.service';
import { Router } from '@angular/router';
import { SettingsService } from 'src/app/services/settings.service';
import { TranslateService } from '@ngx-translate/core';
import { environment } from 'src/environments/environment';
import { IonAccordionGroup, IonSlides } from '@ionic/angular';

@Component({
  selector: 'app-register-school-page',
  templateUrl: './register-school-page.component.html',
  styleUrls: ['./register-school-page.component.scss'],
})
export class RegisterSchoolPageComponent implements OnInit {
  @ViewChild(IonAccordionGroup, { static: true })
  accordionGroup: IonAccordionGroup;
  @ViewChild('mySlider') slides: IonSlides;
  schools: any;
  schoolId: any;
  slideOpts = {
    initialSlide: 0,
    speed: 400,
    pagination: {
      el: '.swiper-pagination', // target class for bullets
      clickable: true
    }

  };
  isFirst = true;

  isLast = false;
  appName = environment.appName;
  privacyUrl1 = "https://opendatacommons.org/licenses/odbl/1-0/";
  privacyUrl2 = "https://www.measurementlab.net/privacy/";
  targetUrl = "_blank"
  isPrivacyChecked = false;
  constructor(public loading: LoadingService,
    private readonly router: Router,
    private settingsService: SettingsService,
    private translate: TranslateService

  ) {
    const appLang = this.settingsService.get('applicationLanguage');
    this.translate.use(appLang.code);
  }

  ngOnInit() { }

  swipeNext() {
    this.slides.slideNext();
  }
  reachedEnd() {
    this.isLast = true;
  }
  moveToStartTest() {
    const translatedText = this.translate.instant('searchCountry.check');

    const loadingMsg =
      // eslint-disable-next-line max-len
      `<div class="loadContent"><ion-img src="assets/loader/new_loader.gif" class="loaderGif"></ion-img><p class="green_loader" >${translatedText}</p></div>`;
    this.loading.present(loadingMsg, 3000, 'pdcaLoaderClass', 'null');
    this.router.navigate(['/searchcountry']);
  }

  redirectToCountry() {
    const translatedText = this.translate.instant('searchCountry.check');

    const loadingMsg =
      // eslint-disable-next-line max-len
      `<div class="loadContent"><ion-img src="assets/loader/new_loader.gif" class="loaderGif"></ion-img><p class="green_loader" >${translatedText}</p></div>`;
    this.loading.present(loadingMsg, 3000, 'pdcaLoaderClass', 'null');
    this.router.navigate(['/searchcountry']);

  }

  openExternalUrl(href) {
    this.settingsService.getShell().shell.openExternal(href);
  }
  async checkCurrentSlide() {
    const index = await this.slides.getActiveIndex();
    this.isFirst = index === 0;
  }

}
