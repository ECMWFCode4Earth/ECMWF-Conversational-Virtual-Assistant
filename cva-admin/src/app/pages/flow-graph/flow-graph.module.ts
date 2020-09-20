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
    NgxEchartsModule.forRoot({
      /**
       * This will import all modules from echarts.
       * If you only need custom modules,
       * please refer to [Custom Build] section.
       */
      echarts: () => import('echarts'), // or import('./path-to-my-custom-echarts')
    }),
    CommonModule,
  ],
})
export class FlowGraphModule {
}
