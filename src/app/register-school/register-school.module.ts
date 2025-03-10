import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterSchoolPageComponent } from './register-school-page/register-school-page.component';
import { PcdcHeaderComponent } from '../pcdc-header/pcdc-header.component';
import { SharedModule } from '../shared/shared.module';
import { IonicModule } from '@ionic/angular';
import { RegisterSchoolRoutingModule } from './register-school-routing.module';



@NgModule({
  declarations: [RegisterSchoolPageComponent, PcdcHeaderComponent],
  imports: [
    CommonModule,
    SharedModule,
    IonicModule,
    RegisterSchoolRoutingModule
  ]
})
export class RegisterSchoolModule { }
