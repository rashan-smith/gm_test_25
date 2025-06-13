import { Component, ViewChild, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../services/storage.service';
import { SettingsService } from '../services/settings.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { IonInput } from '@ionic/angular';
import { LoadingService } from '../services/loading.service';
import { SchoolService } from '../services/school.service';
import { School } from '../models/models';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-schoolemail',
  templateUrl: 'schoolemail.page.html',
  styleUrls: ['schoolemail.page.scss'],
})
export class SchoolemailPage implements OnDestroy {
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
  private destroy$ = new Subject<void>();

  get emailFormArray() {
    return this.emailForm.get('emails') as FormArray;
  }

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

    this.sub = this.activatedroute.params
      .pipe(takeUntil(this.destroy$))
      .subscribe((params) => {
        if (this.router.getCurrentNavigation()) {
          this.school = this.router.getCurrentNavigation().extras.state as School;
          this.schoolData = this.router.getCurrentNavigation().extras.state?.schoolData;
        }
      });
    
    this.initForm();
  }

  initForm() {
    this.emailForm = this.formBuilder.group({
      emails: this.formBuilder.array([
        this.createEmailFormGroup()
      ])
    });
  }

  createEmailFormGroup(): FormGroup {
    return this.formBuilder.group({
      email: ['', [Validators.email]]
    });
  }

  addEmail() {
    if (this.emailFormArray.length < 3) {
      this.emailFormArray.push(this.createEmailFormGroup());
      // Use setTimeout to ensure the view updates properly
      setTimeout(() => {
        const lastInput = document.querySelector('.email-input-group:last-child ion-input');
        if (lastInput) {
          (lastInput as HTMLElement).focus();
        }
      });
    }
  }

  removeEmail(index: number) {
    this.emailFormArray.removeAt(index);
  }

  onSubmit() {
    if (this.emailForm.valid) {
      const emailValues = this.emailFormArray.controls
        .map(control => control.get('email').value)
        .filter(email => email && email.trim());

      if (emailValues.length > 0) {
        // Store the first email as primary
        this.storageService.set('email', emailValues[0]);
        console.log('primary email', emailValues[0]);
        
        // Store all emails as a comma-separated string
        this.storageService.set('all_emails', emailValues.join(','));
        
        if (this.selectedSchool) {
          this.selectedSchool.email = emailValues[0];
        }

        const loadingMsg = '<div class="loadContent"><ion-img src="assets/loader/loader.gif" class="loaderGif"></ion-img><p class="white">Loading...</p></div>';
        this.loading.present(loadingMsg, 4000, 'pdcaLoaderClass', 'null');

        // Register the school device with email
        const schoolData = {
          mac_address: localStorage.getItem('macAddress'),
          email: emailValues // Send all emails as an array
        };

        this.schoolService
          .updateSchoolDeviceWithEmail(schoolData)
          .pipe(takeUntil(this.destroy$))
          .subscribe(
            (response) => {
              this.storage.set('email', emailValues[0]);
              this.storage.set('all_emails', emailValues.join(','));
              this.loading.dismiss();
              this.router.navigate(['/schoolsuccess']);
              this.settings.setSetting('scheduledTesting', true);
            },
            (err) => {
              this.loading.dismiss();
              this.submissionError = 'schoolEmail.submissionError';
            }
          );
      }
    }
  }

  skipEmail() {
    this.router.navigate(['/schoolsuccess']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
    
