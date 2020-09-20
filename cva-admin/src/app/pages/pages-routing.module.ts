import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';

import {PagesComponent} from './pages.component';
import {NotFoundComponent} from './miscellaneous/not-found/not-found.component';
import {KpiDashboardComponent} from './kpi-dashboard/kpi-dashboard.component';
import {FlowGraphComponent} from './flow-graph/flow-graph.component';

const routes: Routes = [{
  path: '',
  component: PagesComponent,
  children: [
    {
      path: 'kpi-dashboard',
      component: KpiDashboardComponent,
    },
    {
      path: 'flow-graph',
      component: FlowGraphComponent,
    },
    {
      path: 'miscellaneous',
      loadChildren: () => import('./miscellaneous/miscellaneous.module')
        .then(m => m.MiscellaneousModule),
    },
    {
      path: '',
      redirectTo: 'kpi-dashboard',
      pathMatch: 'full',
    },
    {
      path: '**',
      component: NotFoundComponent,
    },
  ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {
}
