import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FlowGraphChartComponent} from './flow-graph-chart/flow-graph-chart.component';
import {NgxEchartsModule} from 'ngx-echarts';


@NgModule({
  declarations: [FlowGraphChartComponent],
  exports: [
    FlowGraphChartComponent,
  ],
  imports: [
    NgxEchartsModule,
    CommonModule,
  ],
})
export class FlowGraphModule {
}
