import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaHealthCheckComponent } from './health.component';
import { GhaHealthModalComponent } from './health-modal.component';

import { healthRoute } from './health.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([healthRoute])],
  declarations: [GhaHealthCheckComponent, GhaHealthModalComponent],
  entryComponents: [GhaHealthModalComponent]
})
export class HealthModule {}
