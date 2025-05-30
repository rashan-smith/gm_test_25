import { Component, ViewChild } from '@angular/core';
import { IonAccordionGroup } from '@ionic/angular';
import { Router } from '@angular/router';
import { LoadingService } from '../services/loading.service';
import { IonSlides } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { SettingsService } from '../services/settings.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-schoolsuccess',
  templateUrl: 'schoolsuccess.page.html',
  styleUrls: ['schoolsuccess.page.scss'],
})
export class SchoolsuccessPage {
  @ViewChild(IonAccordionGroup, { static: true })
  accordionGroup: IonAccordionGroup;
  @ViewChild('mySlider') slides: IonSlides;
  schools: any;
  schoolId: any;
  slideOpts = {
    speed: 400,
  };
  isLast = false;
  appName = environment.appName;
  constructor(
    public loading: LoadingService,
    private router: Router,
    private translate: TranslateService,
    private settingsService: SettingsService
  ) {
    const appLang = this.settingsService.get('applicationLanguage');
    this.translate.use(appLang.code);
  }
  swipeNext() {
    this.slides.slideNext();
  }
  reachedEnd() {
    this.isLast = true;
  }
  moveToStartTest() {
    this.router.navigate(['starttest']);
  }
}
