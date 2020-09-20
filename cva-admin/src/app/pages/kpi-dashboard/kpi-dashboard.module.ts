import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ValidationCardComponent} from './validation-card/validation-card.component';
import {NbCardModule, NbListModule} from '@nebular/theme';
import {IntentListComponent} from './intent-list/intent-list.component';
import {SessionKpiChartComponent} from './session-kpi-chart/session-kpi-chart.component';
import {NgxEchartsModule} from "ngx-echarts";

@NgModule({
  declarations: [ValidationCardComponent, IntentListComponent, SessionKpiChartComponent],
  imports: [
    CommonModule,
    NbCardModule,
    NbListModule,
    NgxEchartsModule,
  ],
  exports: [
    ValidationCardComponent,
    IntentListComponent,
    SessionKpiChartComponent,
  ],
})
export class KpiDashboardModule {
}
