import {NgModule} from '@angular/core';
import {NbButtonModule, NbCardModule, NbIconModule, NbMenuModule, NbProgressBarModule} from '@nebular/theme';

import {ThemeModule} from '../@theme/theme.module';
import {PagesComponent} from './pages.component';
import {DashboardModule} from './dashboard/dashboard.module';
import {ECommerceModule} from './e-commerce/e-commerce.module';
import {PagesRoutingModule} from './pages-routing.module';
import {MiscellaneousModule} from './miscellaneous/miscellaneous.module';
import {KpiDashboardComponent} from './kpi-dashboard/kpi-dashboard.component';
import {KpiDashboardModule} from "./kpi-dashboard/kpi-dashboard.module";
import {ChartsModule} from "./charts/charts.module";
import {FlowGraphComponent} from './flow-graph/flow-graph.component';
import {FlowGraphModule} from "./flow-graph/flow-graph.module";

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    DashboardModule,
    ECommerceModule,
    MiscellaneousModule,
    NbCardModule,
    NbIconModule,
    NbButtonModule,
    KpiDashboardModule,
    NbProgressBarModule,
    ChartsModule,
    FlowGraphModule,
  ],
  declarations: [
    PagesComponent,
    KpiDashboardComponent,
    FlowGraphComponent,
  ],
})
export class PagesModule {
}
