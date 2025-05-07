import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-selected-detail',
  templateUrl: './selected-detail.component.html',
  styleUrls: ['./selected-detail.component.scss'],
})
export class SelectedDetailComponent implements OnInit {
  private sub: any;
  schoolId: any;
  selectedSchool: any;
  detectedCountry: any;
  selectedCountry: any;
  showSchoolId = true;
  constructor( private activatedroute: ActivatedRoute, private readonly router: Router ) { }

  ngOnInit() {
    if(this.router.url.includes('schoolnotfound')) {
      this.showSchoolId = false;
    }
    this.sub = this.activatedroute.params.subscribe((params) => {
      this.schoolId = params.schoolId;
      this.selectedCountry = params.selectedCountry;
      this.detectedCountry = params.detectedCountry;
      this.selectedSchool = {};
    });
   
  }

}
