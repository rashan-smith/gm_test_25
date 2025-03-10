import { Component, OnInit } from '@angular/core';
import { LoadingService } from '../../services/loading.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register-school-page',
  templateUrl: './register-school-page.component.html',
  styleUrls: ['./register-school-page.component.scss'],
})
export class RegisterSchoolPageComponent implements OnInit {
  privacyUrl1 = "https://opendatacommons.org/licenses/odbl/1-0/";
  privacyUrl2= "https://www.measurementlab.net/privacy/";
  targetUrl="_blank"
  isPrivacyChecked = false;
  constructor(    public loading: LoadingService,
    private readonly router: Router
  ) { }

  ngOnInit() {}

  toggleCheckbox() {
    this.isPrivacyChecked = !this.isPrivacyChecked;
  }

  redirectToCountry(){
    const loadingMsg =
      // eslint-disable-next-line max-len
      `<div class="loadContent"><ion-img src="assets/images/country_loader.png" class="loaderGif"></ion-img><p class="green_loader" [translate]="'searchCountry.check'">Detecting your country</p></div>`;
    this.loading.present(loadingMsg, 3000, 'pdcaLoaderClass', 'null');
    this.router.navigate(['/searchcountry']);

  }
  

}
