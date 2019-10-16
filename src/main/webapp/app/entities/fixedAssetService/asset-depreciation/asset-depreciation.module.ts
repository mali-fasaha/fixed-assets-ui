import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { AssetDepreciationComponent } from './asset-depreciation.component';
import { AssetDepreciationDetailComponent } from './asset-depreciation-detail.component';
import { AssetDepreciationUpdateComponent } from './asset-depreciation-update.component';
import {
  AssetDepreciationDeletePopupComponent,
  AssetDepreciationDeleteDialogComponent
} from './asset-depreciation-delete-dialog.component';
import { assetDepreciationRoute, assetDepreciationPopupRoute } from './asset-depreciation.route';

const ENTITY_STATES = [...assetDepreciationRoute, ...assetDepreciationPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    AssetDepreciationComponent,
    AssetDepreciationDetailComponent,
    AssetDepreciationUpdateComponent,
    AssetDepreciationDeleteDialogComponent,
    AssetDepreciationDeletePopupComponent
  ],
  entryComponents: [AssetDepreciationDeleteDialogComponent]
})
export class FixedAssetServiceAssetDepreciationModule {}
