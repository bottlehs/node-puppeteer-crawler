import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { RootPage } from './root.page';

const routes: Routes = [
  {
    path: '',
    component: RootPage
  }
];

@NgModule({
  imports: [
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [RootPage]
})
export class RootPageModule {}
