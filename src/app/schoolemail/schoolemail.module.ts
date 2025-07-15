import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { SchoolemailPage } from './schoolemail.page';
import { PcdcHeaderComponent } from '../pcdc-header/pcdc-header.component';
import { SchoolemailPageRoutingModule } from './schoolemail-routing.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ReactiveFormsModule,
    TranslateModule,
    SchoolemailPageRoutingModule,
  ],
  declarations: [SchoolemailPage, PcdcHeaderComponent]
})
export class SchoolemailPageModule {}