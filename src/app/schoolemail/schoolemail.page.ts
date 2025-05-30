import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../services/storage.service';
import { SettingsService } from '../services/settings.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonInput } from '@ionic/angular';
import { LoadingService } from '../services/loading.service';
import { SchoolService } from '../services/school.service';
import { School } from '../models/models';

@Component({
  selector: 'app-schoolemail',
  templateUrl: 'schoolemail.page.html',
  styleUrls: ['schoolemail.page.scss'],
})
export class SchoolemailPage {
  @ViewChild(IonInput) emailInput: IonInput;
  
  emailForm: FormGroup;
  schoolId: string;
  selectedCountry: string;
  detectedCountry: string;
  selectedSchool: any = {};
  schoolData: any;
  sub: any;
  school: any;
  submissionError: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private activatedroute: ActivatedRoute,
    private router: Router,
    private datePipe: DatePipe,
    public loading: LoadingService,
    private settings: SettingsService,
    private translate: TranslateService,
    private storageService: StorageService,
    private schoolService: SchoolService,
    private storage: StorageService
  ) {
    const appLang = this.settings.get('applicationLanguage');
    this.translate.use(appLang.code);

    this.sub = this.activatedroute.params.subscribe((params) => {
      if (this.router.getCurrentNavigation()) {
        this.school = this.router.getCurrentNavigation().extras.state as School;
        this.schoolData = this.router.getCurrentNavigation().extras.state?.schoolData;
      }
    });
    
    this.emailForm = this.formBuilder.group({
      email: ['', [Validators.email]]
    });
  }

  onSubmit() {
    if (this.emailForm.valid) {
      const emailValue = this.emailForm.get('email').value;
      if (emailValue && emailValue.trim()) {
        this.storageService.set('email', emailValue);
        console.log('email', emailValue);
        
        if (this.selectedSchool) {
          this.selectedSchool.email = emailValue;
        }
      }

      const loadingMsg = '<div class="loadContent"><ion-img src="assets/loader/loader.gif" class="loaderGif"></ion-img><p class="white">Loading...</p></div>';
      this.loading.present(loadingMsg, 4000, 'pdcaLoaderClass', 'null');

      // Register the school device with email
      const schoolData = {
        mac_address: localStorage.getItem('macAddress'),
        email: emailValue
      };

      this.schoolService
        .updateSchoolDeviceWithEmail(schoolData)
        .subscribe(
          (response) => {
            this.storage.set('email', emailValue);
            this.loading.dismiss();
            this.router.navigate(['/schoolsuccess']);
            this.settings.setSetting('scheduledTesting', true);
          }),
          (err) => {
            this.loading.dismiss();
            this.submissionError = 'schoolEmail.submissionError';
          };
      }
    }

  skipEmail() {
    this.router.navigate(['/schoolsuccess']);
  }
}
    
