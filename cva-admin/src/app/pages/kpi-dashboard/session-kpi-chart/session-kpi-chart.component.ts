import {Component, Input, OnInit} from '@angular/core';
import {ConversionStep} from '../kpi-dashboard.component';

@Component({
  selector: 'ngx-session-kpi-chart',
  templateUrl: './session-kpi-chart.component.html',
  styleUrls: ['./session-kpi-chart.component.scss'],
})
export class SessionKpiChartComponent implements OnInit {


  @Input() conversionSessionStats: ConversionStep[];

  options: any;

  constructor() {
  }


  ngOnInit(): void {
    const xAxisData = [];
    const data1 = [];
    const data2 = [];

    this.conversionSessionStats.forEach((s: ConversionStep) => {
      xAxisData.push(s.localDate);
      data1.push(s.sessions);
      data2.push(s.fallbacks);
    });

    this.options = {
      legend: {
        data: ['All sessions', 'Fallback sessions'],
        align: 'left',
      },
      tooltip: {},
      xAxis: {
        data: xAxisData,
        silent: false,
        splitLine: {
          show: false,
        },
      },
      yAxis: {},
      series: [
        {
          name: 'All sessions',
          type: 'line',
          data: data1,
          animationDelay: (idx) => idx * 10,
        },
        {
          name: 'Fallback sessions',
          type: 'bar',
          data: data2,
          animationDelay: (idx) => idx * 10 + 100,
        },
      ],
      animationEasing: 'elasticOut',
      animationDelayUpdate: (idx) => idx * 5,
    };
  }
}
