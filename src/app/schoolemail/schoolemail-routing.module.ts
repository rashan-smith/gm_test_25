import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SchoolemailPage } from './schoolemail.page';

const routes: Routes = [
  {
    path: '',
    component: SchoolemailPage,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SchoolemailPageRoutingModule {}
