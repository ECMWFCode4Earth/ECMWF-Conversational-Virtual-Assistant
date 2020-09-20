import {Component, Input} from '@angular/core';

@Component({
  selector: 'ngx-validation-card',
  styleUrls: ['./validation-card.component.scss'],
  template: `
    <nb-card>
      <div class="icon-container">
        <div class="icon" [ngClass]="isValid ? 'status-success nb-checkmark' : 'status-danger nb-alert'">
          <ng-content></ng-content>
        </div>
      </div>

      <div class="details">
        <div class="title h5">{{ title }}</div>
        <div class="status paragraph-2">{{ isValid ? 'All good' : 'Error' }}</div>
      </div>
    </nb-card>
  `,
})
export class ValidationCardComponent {

  @Input() title: string;
  @Input() icon: string;
  @Input() isValid = true;

}
