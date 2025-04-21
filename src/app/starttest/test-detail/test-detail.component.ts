import { Component, OnInit } from '@angular/core';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-test-detail',
  templateUrl: './test-detail.component.html',
  styleUrls: ['./test-detail.component.scss'],
})
export class TestDetailComponent implements OnInit {
  schoolId: string;
  historicalData: any;
  measurementsData: []
  constructor(    private storage: StorageService) { }

  ngOnInit() {    
    this.schoolId = this.storage.get('schoolId');
    if(this.storage.get('historicalDataAll')) {
      this.historicalData =  JSON.parse(this.storage.get('historicalDataAll'))
      this.measurementsData = this.historicalData.measurements
    }
    

  }

}
