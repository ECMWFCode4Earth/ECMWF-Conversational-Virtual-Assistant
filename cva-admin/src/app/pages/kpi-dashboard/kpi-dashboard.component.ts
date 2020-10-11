import {Component, OnDestroy, OnInit} from '@angular/core';
import {KpiDashboardService} from './kpi-dashboard.service';
import {Observable} from 'rxjs';
import {AgentChangeService} from '../../agent-change.service';

export interface IntentError {
  displayName: string;
  errors: string[];
}

export interface ConversionStep {
  localDate: string;
  sessions: number;
  fallbacks: number;
}

@Component({
  selector: 'ngx-kpi-dashboard',
  templateUrl: './kpi-dashboard.component.html',
  styleUrls: ['./kpi-dashboard.component.scss'],
})
export class KpiDashboardComponent implements OnInit, OnDestroy {

  private alive = true;

  intentCount$: Observable<number>;
  trainingSentencesCount$: Observable<number>;
  intentErrors$: Observable<IntentError[]>;
  conversionSessionStats$: Observable<ConversionStep[]>;
  agent: string;
  type = 'month';
  types = ['week', 'month', 'year'];

  constructor(
    private kds: KpiDashboardService,
    private acs: AgentChangeService,
  ) {
  }

  ngOnInit(): void {
    this.acs.selectedAgent$.subscribe((agent: string) => this.initDashBoard(agent));
  }

  private initDashBoard(agent: string) {
    this.agent = agent;
    this.retrieveIntentCount(agent);
    this.retrieveTrainingSentencesCount(agent);
    this.loadIntentErrors(agent);
    this.retrieveConversionSessionStats(agent, this.type);
  }

  ngOnDestroy() {
    this.alive = true;
  }

  private loadIntentErrors(agent: string) {
    this.intentErrors$ = this.kds.findAllIntentErrors(agent);
  }

  private retrieveIntentCount(agent: string) {
    this.intentCount$ = this.kds.intentCount(agent);
  }

  private retrieveTrainingSentencesCount(agent: string) {
    this.trainingSentencesCount$ = this.kds.trainingSentencesCount(agent);
  }

  private retrieveConversionSessionStats(agent: string, type: string) {
    this.conversionSessionStats$ = this.kds.conversionSessionStats(agent, type);
  }

  dateSelectionChanged($event: any) {
    this.retrieveConversionSessionStats(this.agent, $event);
  }
}
