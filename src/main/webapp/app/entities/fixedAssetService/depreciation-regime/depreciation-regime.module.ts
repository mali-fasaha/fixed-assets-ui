import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { DepreciationRegimeComponent } from './depreciation-regime.component';
import { DepreciationRegimeDetailComponent } from './depreciation-regime-detail.component';
import { DepreciationRegimeUpdateComponent } from './depreciation-regime-update.component';
import {
  DepreciationRegimeDeletePopupComponent,
  DepreciationRegimeDeleteDialogComponent
} from './depreciation-regime-delete-dialog.component';
import { depreciationRegimeRoute, depreciationRegimePopupRoute } from './depreciation-regime.route';

const ENTITY_STATES = [...depreciationRegimeRoute, ...depreciationRegimePopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    DepreciationRegimeComponent,
    DepreciationRegimeDetailComponent,
    DepreciationRegimeUpdateComponent,
    DepreciationRegimeDeleteDialogComponent,
    DepreciationRegimeDeletePopupComponent
  ],
  entryComponents: [DepreciationRegimeDeleteDialogComponent]
})
export class FixedAssetServiceDepreciationRegimeModule {}
