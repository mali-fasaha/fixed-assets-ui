import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { AssetDisposalComponent } from './asset-disposal.component';
import { AssetDisposalDetailComponent } from './asset-disposal-detail.component';
import { AssetDisposalUpdateComponent } from './asset-disposal-update.component';
import { AssetDisposalDeletePopupComponent, AssetDisposalDeleteDialogComponent } from './asset-disposal-delete-dialog.component';
import { assetDisposalRoute, assetDisposalPopupRoute } from './asset-disposal.route';

const ENTITY_STATES = [...assetDisposalRoute, ...assetDisposalPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    AssetDisposalComponent,
    AssetDisposalDetailComponent,
    AssetDisposalUpdateComponent,
    AssetDisposalDeleteDialogComponent,
    AssetDisposalDeletePopupComponent
  ],
  entryComponents: [AssetDisposalDeleteDialogComponent]
})
export class FixedAssetServiceAssetDisposalModule {}
