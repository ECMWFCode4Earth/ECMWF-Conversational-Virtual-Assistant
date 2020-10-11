import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ConversionStep, IntentError} from './kpi-dashboard.component';

@Injectable({
  providedIn: 'root',
})
export class KpiDashboardService {

  constructor(
    private http: HttpClient,
  ) {
  }


  findAllIntentErrors(agent: string): Observable<IntentError[]> {
    return this.http.get<IntentError[]>(`/api/dashboard/intent-health/${agent}`);
  }

  intentCount(agent: string): Observable<number> {
    return this.http.get<number>(`/api/dashboard/intents-count/${agent}`);
  }


  trainingSentencesCount(agent: string): Observable<number> {
    return this.http.get<number>(`/api/dashboard/training-sentences-count/${agent}`);
  }

  conversionSessionStats(agent: string, type: string): Observable<ConversionStep[]> {
    return this.http.get<ConversionStep[]>(`/api/dashboard/conversion-session-stats/${agent}/${type}`);
  }


}
