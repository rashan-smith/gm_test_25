import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SchoolEmailRegisterRoutingModule } from './school-email-register-routing.module';
import { SharedModule } from '../shared/shared.module';
import { IonicModule } from '@ionic/angular';
import { PcdcHeaderComponent } from '../pcdc-header/pcdc-header.component';
import { SaveSchoolEmailComponent } from './save-school-email/save-school-email.component';



@NgModule({
  declarations: [PcdcHeaderComponent, SaveSchoolEmailComponent],
  imports: [
    CommonModule,
    SchoolEmailRegisterRoutingModule,
    SharedModule,
    IonicModule,
  ]
})
export class SchoolEmailRegisterModule { }
