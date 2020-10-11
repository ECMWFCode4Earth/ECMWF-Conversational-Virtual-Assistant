import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {parse} from 'echarts/extension/dataTool/gexf';
import {AgentChangeService} from '../../../agent-change.service';

@Component({
  selector: 'ngx-flow-graph-chart',
  templateUrl: './flow-graph-chart.component.html',
  styleUrls: ['./flow-graph-chart.component.scss'],
})
export class FlowGraphChartComponent implements OnInit {

  options: any = {};
  themeSubscription: any;


  constructor(
    private http: HttpClient,
    private acs: AgentChangeService,
  ) {
  }

  ngOnInit(): void {
    this.acs.selectedAgent$.subscribe((agent: string) => this.initFlowGraph(agent));
  }


  private initFlowGraph(agent: string) {
    this.options = this.http.get(`/api/flow-graph/index/${agent}`, {responseType: 'text'}).pipe(
      map((xml) => {
        const graph = parse(xml);
        const categories = [];
        categories[0] = {
          name: 'No Events',
        };
        categories[1] = {
          name: 'Contains Events',
        };

        graph.nodes.forEach((node) => {

          node.itemStyle = null;
          node.symbolSize = node.attributes.size;
          node.value = node.attributes.size;
          node.category = node.attributes.category;
          // Use random x, y
          node.x = node.y = null;
          node.draggable = true;
        });
        return {
          title: {
            text: 'CVA flows',
            top: 'bottom',
            left: 'right',
          },
          tooltip: {},
          legend: [
            {
              data: categories.map((a) => a.name),
            },
          ],
          animationDurationUpdate: 1500,
          animationEasingUpdate: 'quinticInOut',
          series: [
            {
              type: 'graph',
              layout: 'force',
              data: graph.nodes,
              links: graph.links,
              categories,
              roam: true,
              label: {
                normal: {
                  position: 'right',
                },
              },
              force: {
                repulsion: 100,
              },

              focusNodeAdjacency: true,
              lineStyle: {
                width: 0.5,
                curveness: 0.3,
                opacity: 0.7,
              },
            },
          ],
        };
      }),
    );
  }
}
