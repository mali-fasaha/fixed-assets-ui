import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaTrackerComponent } from './tracker.component';

import { trackerRoute } from './tracker.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([trackerRoute])],
  declarations: [GhaTrackerComponent]
})
export class TrackerModule {}
