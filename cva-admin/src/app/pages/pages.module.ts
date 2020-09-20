import {NgModule} from '@angular/core';
import {
  NbButtonModule,
  NbCardModule,
  NbIconModule,
  NbMenuModule,
  NbProgressBarModule,
  NbSelectModule,
} from '@nebular/theme';

import {ThemeModule} from '../@theme/theme.module';
import {PagesComponent} from './pages.component';
import {PagesRoutingModule} from './pages-routing.module';
import {MiscellaneousModule} from './miscellaneous/miscellaneous.module';
import {KpiDashboardComponent} from './kpi-dashboard/kpi-dashboard.component';
import {FlowGraphComponent} from './flow-graph/flow-graph.component';
import {FlowGraphModule} from './flow-graph/flow-graph.module';
import {KpiDashboardModule} from './kpi-dashboard/kpi-dashboard.module';

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    MiscellaneousModule,
    NbCardModule,
    NbIconModule,
    NbButtonModule,
    KpiDashboardModule,
    NbProgressBarModule,
    FlowGraphModule,
    NbSelectModule,
  ],
  declarations: [
    PagesComponent,
    KpiDashboardComponent,
    FlowGraphComponent,
  ],
})
export class PagesModule {
}
