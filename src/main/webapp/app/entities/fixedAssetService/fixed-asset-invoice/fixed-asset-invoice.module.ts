import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FixedAssetInvoiceComponent } from './fixed-asset-invoice.component';
import { FixedAssetInvoiceDetailComponent } from './fixed-asset-invoice-detail.component';
import { FixedAssetInvoiceUpdateComponent } from './fixed-asset-invoice-update.component';
import {
  FixedAssetInvoiceDeletePopupComponent,
  FixedAssetInvoiceDeleteDialogComponent
} from './fixed-asset-invoice-delete-dialog.component';
import { fixedAssetInvoiceRoute, fixedAssetInvoicePopupRoute } from './fixed-asset-invoice.route';

const ENTITY_STATES = [...fixedAssetInvoiceRoute, ...fixedAssetInvoicePopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FixedAssetInvoiceComponent,
    FixedAssetInvoiceDetailComponent,
    FixedAssetInvoiceUpdateComponent,
    FixedAssetInvoiceDeleteDialogComponent,
    FixedAssetInvoiceDeletePopupComponent
  ],
  entryComponents: [FixedAssetInvoiceDeleteDialogComponent]
})
export class FixedAssetServiceFixedAssetInvoiceModule {}
