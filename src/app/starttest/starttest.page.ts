import {
  Component,
  OnInit,
  ViewChild,
  ChangeDetectorRef,
  ElementRef,
  OnDestroy,
} from '@angular/core';
import { IonAccordionGroup, ModalController } from '@ionic/angular';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { SchoolService } from '../services/school.service';
import { LoadingService } from '../services/loading.service';
import { MenuController } from '@ionic/angular';
import { NetworkService } from '../services/network.service';
import { SettingsService } from '../services/settings.service';
import { MlabService } from '../services/mlab.service';
import { MeasurementClientService } from '../services/measurement-client.service';
import { SharedService } from '../services/shared-service.service';
import { HistoryService } from '../services/history.service';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../services/storage.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-starttest',
  templateUrl: 'starttest.page.html',
  styleUrls: ['starttest.page.scss'],
})
export class StarttestPage implements OnInit, OnDestroy {
  @ViewChild(IonAccordionGroup, { static: true })
  accordionGroup: IonAccordionGroup;
  @ViewChild('errorMsg') el: ElementRef;
  progress: number = 0; // Progress in percentage (0-100)
  currentState = undefined;
  currentRate = undefined;
  currentRateUpload = undefined;
  currentRateDownload = undefined;
  latency = undefined;
  networkName = undefined;
  uploadStatus = undefined;
  uploadRate = undefined;
  isErrorClosed = false;
  connectionInformation: any;
  lastMeasurementId: number;
  mlabInformation = {
    city: '',
    url: '',
    ip: [],
    fqdn: '',
    site: '',
    country: '',
    label: '',
    metro: '',
  };
  accessInformation = {
    ip: '',
    city: '',
    region: '',
    country: '',
    label: '',
    metro: '',
    site: '',
    url: '',
    fqdn: '',
    loc: '',
    org: '',
    postal: '',
    timezone: '',
    asn: '',
  };
  isLoaded = false;
  downloadStarted = false;
  uploadStarted = false;

  progressGaugeState = {
    type: 'full',
    minimum: 0,
    current: 0,
    maximum: 1,
    message: 'Start',
    foregroundColor: '#FFFFFF',
    backgroundColor: '#FFFF00',
  };
  onlineStatus: boolean;
  schools: any;
  schoolId: any;
  public currentDate: any;
  public connectionStatus: any;
  private sub: any;
  private downloadSub!: Subscription;
  private uploadSub!: Subscription;
  private downloadStartedSub!: Subscription;
  private uploadStartedSub!: Subscription;

  downloadTimer: any;
  uploadTimer: any;
  uploadProgressStarted = false; // To ensure we start upload animation only once

  constructor(
    private route: ActivatedRoute,
    public loading: LoadingService,
    public router: Router,
    private menu: MenuController,
    public alertController: AlertController,
    public modalController: ModalController,
    private schoolService: SchoolService,
    private networkService: NetworkService,
    private settingsService: SettingsService,
    private mlabService: MlabService,
    private measurementClientService: MeasurementClientService,
    private sharedService: SharedService,
    private historyService: HistoryService,
    public translate: TranslateService,
    private ref: ChangeDetectorRef,
    private storage: StorageService
  ) {
    this.onlineStatus = navigator.onLine;
    this.route.params.subscribe((params) => {
      if (this.onlineStatus) {
        this.measureReady();
      }
    });
    let applicationLanguage = this.settingsService.get('applicationLanguage');
    if (applicationLanguage) {
      if (typeof applicationLanguage === 'string') {
        translate.setDefaultLang(applicationLanguage);
      } else {
        translate.setDefaultLang(applicationLanguage.code);
      }
    }

    window.addEventListener(
      'online',
      () => {
        // Re-sync data with server.
        try {
          console.log('Online');
          this.progress = 0;
          this.onlineStatus = true;
          this.currentState = undefined;
          this.currentRate = undefined;
          this.measureReady();
        } catch (e) {
          console.log(e);
        }
      },
      false
    );

    window.addEventListener(
      'offline',
      () => {
        // Queue up events for server.
        this.onlineStatus = false;
        this.connectionStatus = 'error';
        this.currentRate = 'error';
        this.isErrorClosed = false;
      },
      false
    );

    this.sharedService.on('settings:changed', (nameValue) => {
      if (nameValue.name == 'applicationLanguage') {
        translate.use(nameValue.value.code);
      }
      if (nameValue.name == 'metroSelection') {
        this.tryConnectivity();
      }
    });
    if (!this.storage.get('schoolId')) {
      this.router.navigate(['/']);
    }
  }
  ngOnInit() {
    this.schoolId = this.storage.get('schoolId');
    this.downloadSub = this.measurementClientService.downloadComplete$.subscribe(data => {
      console.log('Download completed:', data);
      this.downloadStarted = false;
      if (this.downloadTimer) {
        clearInterval(this.downloadTimer);
      }
      this.progress = 50;
      this.ref.markForCheck();

    });

    this.downloadStartedSub = this.measurementClientService.downloadStarted$.subscribe(data => {
      console.log('Download started:', data);
      this.downloadStarted = true;
      this.uploadStarted = false;
    });
     this.uploadStartedSub = this.measurementClientService.uploadStarted$.subscribe(data => {
      console.log('Upload Started:', data);
      this.uploadStarted = true;
      this.downloadStarted = false
    });

    this.uploadSub = this.measurementClientService.uploadComplete$.subscribe(data => {
      console.log('Upload completed:', data);
      this.uploadStarted = false;
      if (this.uploadTimer) {
        clearInterval(this.uploadTimer);
      }
      this.progress = 100;
      this.ref.markForCheck();

    });
    window.addEventListener(
      'online',
      () => {
        console.log('Online 1');
      },
      false
    );

    window.addEventListener(
      'offline',
      () => {
        console.log('Offline 1');
      },
      false
    );
    this.sharedService.on('measurement:status', this.driveGauge.bind(this));
    this.sharedService.on(
      'history:measurement:change',
      this.refreshHistory.bind(this)
    );
    this.sharedService.on('history:reset', this.refreshHistory.bind(this));
    this.refreshHistory();
  }

  measureReady() {
    try {
      this.tryConnectivity();
      this.isLoaded = true;
    } catch (e) {
      console.log(e);
    }
  }

  tryConnectivity() {
    let loadingMsg =
      '<div class="loadContent"><ion-img src="assets/loader/loader.gif" class="loaderGif"></ion-img><p class="white">Fetching Internet Provider Info...</p></div>';
    this.loading.present(loadingMsg, 15000, 'pdcaLoaderClass', 'null');
    this.networkService.getNetInfo().then((res) => {
      this.connectionStatus = 'success';
      if (this.loading.isStillLoading()) {
        this.loading.dismiss();
      }
    });
  }

  refreshHistory() {
    let data = this.historyService.get();
    this.lastMeasurementId = data.measurements.length - 1;
  }

  openFirst() {
    this.menu.enable(true, 'first');
    this.menu.open('first');
  }

  closeMenu() {
    this.menu.open('end');
  }
  closeError() {
    // this.el.nativeElement.style.display = 'none';
    this.isErrorClosed = true;
  }

  showTestResult() {
    this.router.navigate(['connectivitytest']);
  }

 
  startNDT() {
    try {
      this.currentState = 'Starting';
      this.uploadStatus = undefined;
      this.connectionStatus = '';
      this.uploadProgressStarted = false;
      this.measurementClientService.runTest();
    } catch (e) {
      console.log(e);
    }
  }

  startDownloadProgress() {
    const target = 50;
    const interval = 1500; // Increase interval to slow down updates (1.5 sec per update)
    const step = 0.5; // Reduce step size for a more gradual increase
  
    this.downloadTimer = setInterval(() => {
      if (this.progress < target && this.downloadStarted) {
        this.progress += step; // Increase progress very slowly
        if (this.progress >= target) {
          this.progress = target;
          clearInterval(this.downloadTimer);
        }
        this.ref.detectChanges(); // Ensure UI updates smoothly
      } else {
        clearInterval(this.downloadTimer);
      }
    }, interval);
  }
  
  
  
  // Animate progress from 50 to 100
  startUploadProgress() {
    const target = 100;
    const interval = 200; // update every 50ms
    this.uploadTimer = setInterval(() => {
      if (this.progress < target) {
        this.progress += 1;
        if (this.progress >= target) {
          this.progress = target;
          clearInterval(this.uploadTimer);
        }
        this.ref.markForCheck();
      } else {
        clearInterval(this.uploadTimer);
      }
    }, interval);
  }


  driveGauge(event, data) {
    if (event === 'measurement:status') {
      console.log({ data });
      if(data.testStatus === 'error') {
        this.connectionStatus = 'error';
        this.currentRate = 'error';
      }
      if (data.testStatus === 'onstart') {
        this.currentState = 'Starting';
        this.currentRate = undefined;
        this.currentRateUpload = undefined;
        this.currentRateDownload = undefined;
        this.progress = 0;
      } else if (data.testStatus === 'interval_c2s') {
        console.log('Running Test (Upload)');
        this.currentState = 'Running Test (Upload)';
        this.currentRate = (
          (data.passedResults.Data.TCPInfo.BytesReceived / data.passedResults.Data.TCPInfo.ElapsedTime) *
          8
        ).toFixed(2);
        this.currentRateUpload = this.currentRate;
        if (!this.uploadProgressStarted) {
          this.uploadProgressStarted = true;
          this.startUploadProgress();
        }

      } else if (data.testStatus === 'interval_s2c') {
        this.currentState = 'Running Test (Download)';
        this.currentRate = data.passedResults.Data.MeanClientMbps?.toFixed(2);
        this.currentRateDownload = data.passedResults.Data.MeanClientMbps?.toFixed(2);
        if(this.downloadStarted) {
          this.startDownloadProgress();

        }
      } else if (data.testStatus === 'complete') {
        console.log('hereeeeee1234', data)
        this.currentState = 'Completed';
        this.currentDate = new Date();
        this.currentRate = data.passedResults['NDTResult.S2C'].LastClientMeasurement.MeanClientMbps?.toFixed(2);
        this.currentRateUpload = data.passedResults['NDTResult.C2S'].LastClientMeasurement.MeanClientMbps?.toFixed(2);
        this.currentRateDownload = data.passedResults['NDTResult.S2C'].LastClientMeasurement.MeanClientMbps?.toFixed(2);
        this.progressGaugeState.current = this.progressGaugeState.maximum;
        this.latency = ((data.passedResults['NDTResult.S2C'].LastServerMeasurement.BBRInfo.MinRTT +
          data.passedResults['NDTResult.C2S'].LastServerMeasurement.BBRInfo.MinRTT) / 2 / 1000).toFixed(0);
        // let historicalData = this.historyService.get();
        // if (historicalData !== null && historicalData !== undefined) {
        //   this.accessInformation =
        //     historicalData.measurements[0].accessInformation;
        // }
        this.ref.markForCheck();
        this.refreshHistory();
      } else if (data.testStatus === 'onerror') {
        this.gaugeError();
        this.currentState = undefined;
        this.currentRate = undefined;
        this.ref.markForCheck();
      }
      if (data.testStatus !== 'complete') {
        this.progressGaugeState.current = data.progress;
      }
    }
  }
  async presentTestFailModal() {
    const alert = await this.alertController.create({
      cssClass: 'my-custom-class',
      header: this.translate.instant('TEST FAILED'),
      message:
        '<strong>' +
        this.translate.instant(
          'The connection was interupted before testing could be completed.'
        ) +
        '</strong>',
      buttons: [
        {
          text: 'Okay',
          handler: () => { },
        },
      ],
    });
    await alert.present();
  }

  async presentAlertConfirm() {
    const alert = await this.alertController.create({
      cssClass: 'my-custom-class',
      header: this.translate.instant('Error'),
      message:
        '<strong>' +
        this.translate.instant(
          'Measurement server is not responding. Please close the app from system tray and try after sometime'
        ) +
        '</strong>',
      buttons: [
        {
          text: 'Okay',
          handler: () => { },
        },
      ],
    });
    await alert.present();
  }

  gaugeError() {
    this.progressGaugeState.current = this.progressGaugeState.maximum;
  }
  ngOnDestroy() {
    this.downloadSub.unsubscribe();
    this.uploadSub.unsubscribe();
    this.downloadStartedSub.unsubscribe();
    this.uploadStartedSub.unsubscribe();
  } 
}
