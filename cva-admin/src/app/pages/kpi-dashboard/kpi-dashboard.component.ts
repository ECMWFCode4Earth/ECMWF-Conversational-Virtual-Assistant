import {Component, OnDestroy, OnInit} from '@angular/core';
import {ProgressInfo} from '../../@core/data/stats-progress-bar';

interface CardSettings {
  title: string;
  iconClass: string;
  type: string;
}

@Component({
  selector: 'ngx-kpi-dashboard',
  templateUrl: './kpi-dashboard.component.html',
  styleUrls: ['./kpi-dashboard.component.scss'],
})
export class KpiDashboardComponent implements OnInit, OnDestroy {

  private alive = true;

  progressInfoData: ProgressInfo[] = [];

  constructor() {
    this.progressInfoData.push({title: 'Number of intents', value: 123, activeProgress: 100, description: 'something'});
  }

  ngOnInit(): void {
  }


  ngOnDestroy() {
    this.alive = true;
  }


}
