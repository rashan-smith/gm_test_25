import { Component, OnInit } from '@angular/core';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-test-detail',
  templateUrl: './test-detail.component.html',
  styleUrls: ['./test-detail.component.scss'],
})
export class TestDetailComponent implements OnInit {
  schoolId: string;
  constructor(    private storage: StorageService) { }

  ngOnInit() {    
    this.schoolId = this.storage.get('schoolId');
  }

}
