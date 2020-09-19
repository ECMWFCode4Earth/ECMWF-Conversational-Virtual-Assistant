import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ValidationCardComponent} from './validation-card/validation-card.component';
import {NbCardModule} from '@nebular/theme';

@NgModule({
  declarations: [ValidationCardComponent],
  imports: [
    CommonModule,
    NbCardModule,
  ],
  exports: [
    ValidationCardComponent,
  ],
})
export class KpiDashboardModule {
}
