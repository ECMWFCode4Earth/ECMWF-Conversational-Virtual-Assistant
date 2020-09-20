import {Component, Input, OnInit} from '@angular/core';
import {IntentError} from '../kpi-dashboard.component';


@Component({
  selector: 'ngx-intent-list',
  templateUrl: './intent-list.component.html',
  styleUrls: ['./intent-list.component.scss'],
})
export class IntentListComponent implements OnInit {

  @Input() intentErrors: IntentError[];

  constructor() {
  }

  ngOnInit(): void {
  }


}
