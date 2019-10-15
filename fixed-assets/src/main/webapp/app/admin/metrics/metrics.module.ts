import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaMetricsMonitoringComponent } from './metrics.component';

import { metricsRoute } from './metrics.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([metricsRoute])],
  declarations: [GhaMetricsMonitoringComponent]
})
export class MetricsModule {}
