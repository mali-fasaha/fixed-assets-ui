import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { AssetAcquisitionComponent } from './asset-acquisition.component';
import { AssetAcquisitionDetailComponent } from './asset-acquisition-detail.component';
import { AssetAcquisitionUpdateComponent } from './asset-acquisition-update.component';
import { AssetAcquisitionDeletePopupComponent, AssetAcquisitionDeleteDialogComponent } from './asset-acquisition-delete-dialog.component';
import { assetAcquisitionRoute, assetAcquisitionPopupRoute } from './asset-acquisition.route';

const ENTITY_STATES = [...assetAcquisitionRoute, ...assetAcquisitionPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    AssetAcquisitionComponent,
    AssetAcquisitionDetailComponent,
    AssetAcquisitionUpdateComponent,
    AssetAcquisitionDeleteDialogComponent,
    AssetAcquisitionDeletePopupComponent
  ],
  entryComponents: [AssetAcquisitionDeleteDialogComponent]
})
export class FixedAssetServiceAssetAcquisitionModule {}
