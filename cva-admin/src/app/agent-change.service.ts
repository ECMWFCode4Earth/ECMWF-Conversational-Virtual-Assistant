import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AgentChangeService {

  selectedAgent$ = new BehaviorSubject('c3s');

  agents = [
    {
      value: 'c3s',
      name: 'C3S',
    },
    {
      value: 'cams',
      name: 'CAMS',
    },
    {
      value: 'ecmwf',
      name: 'ECMWF',
    },
  ];

  constructor() {
  }

  changeAgent(agent: string) {
    this.selectedAgent$.next(agent);
  }
}
