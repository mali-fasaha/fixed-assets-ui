import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { CapitalWorkInProgressComponent } from './capital-work-in-progress.component';
import { CapitalWorkInProgressDetailComponent } from './capital-work-in-progress-detail.component';
import { CapitalWorkInProgressUpdateComponent } from './capital-work-in-progress-update.component';
import {
  CapitalWorkInProgressDeletePopupComponent,
  CapitalWorkInProgressDeleteDialogComponent
} from './capital-work-in-progress-delete-dialog.component';
import { capitalWorkInProgressRoute, capitalWorkInProgressPopupRoute } from './capital-work-in-progress.route';

const ENTITY_STATES = [...capitalWorkInProgressRoute, ...capitalWorkInProgressPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    CapitalWorkInProgressComponent,
    CapitalWorkInProgressDetailComponent,
    CapitalWorkInProgressUpdateComponent,
    CapitalWorkInProgressDeleteDialogComponent,
    CapitalWorkInProgressDeletePopupComponent
  ],
  entryComponents: [CapitalWorkInProgressDeleteDialogComponent]
})
export class FixedAssetServiceCapitalWorkInProgressModule {}
