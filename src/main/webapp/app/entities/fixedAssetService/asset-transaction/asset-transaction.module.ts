import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { AssetTransactionComponent } from './asset-transaction.component';
import { AssetTransactionDetailComponent } from './asset-transaction-detail.component';
import { AssetTransactionUpdateComponent } from './asset-transaction-update.component';
import { AssetTransactionDeletePopupComponent, AssetTransactionDeleteDialogComponent } from './asset-transaction-delete-dialog.component';
import { assetTransactionRoute, assetTransactionPopupRoute } from './asset-transaction.route';

const ENTITY_STATES = [...assetTransactionRoute, ...assetTransactionPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    AssetTransactionComponent,
    AssetTransactionDetailComponent,
    AssetTransactionUpdateComponent,
    AssetTransactionDeleteDialogComponent,
    AssetTransactionDeletePopupComponent
  ],
  entryComponents: [AssetTransactionDeleteDialogComponent]
})
export class FixedAssetServiceAssetTransactionModule {}
