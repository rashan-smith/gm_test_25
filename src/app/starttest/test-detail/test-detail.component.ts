import { Component, OnInit } from '@angular/core';
import { NetworkService } from 'src/app/services/network.service';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-test-detail',
  templateUrl: './test-detail.component.html',
  styleUrls: ['./test-detail.component.scss'],
  standalone: false
  
})
export class TestDetailComponent implements OnInit {
  schoolId: string;
  historicalData: any;
  measurementsData: []
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
  constructor(    private storage: StorageService,
    private networkService: NetworkService

  ) { }

  ngOnInit() {    
    this.networkService.getNetInfo().then((res) => {
      
      if (res) {
        this.accessInformation = res;
        console.log(this.accessInformation)
      }
    });
    this.schoolId = this.storage.get('schoolId');
    // if(this.storage.get('historicalDataAll')) {
    //   this.historicalData =  JSON.parse(this.storage.get('historicalDataAll'))
    //   this.measurementsData = this.historicalData.measurements
    // }

    if (this.storage.get('historicalDataAll')) {
      this.historicalData = JSON.parse(this.storage.get('historicalDataAll'));
      const allMeasurements = this.historicalData.measurements;
  
      // Get the last 10 measurements (sorted by timestamp descending)
      this.measurementsData = allMeasurements
        .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()) // descending order
        .slice(0, 10); // take last 10
    }
    

  }

}
