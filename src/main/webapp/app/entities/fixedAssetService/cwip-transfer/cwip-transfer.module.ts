import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { CwipTransferComponent } from './cwip-transfer.component';
import { CwipTransferDetailComponent } from './cwip-transfer-detail.component';
import { CwipTransferUpdateComponent } from './cwip-transfer-update.component';
import { CwipTransferDeletePopupComponent, CwipTransferDeleteDialogComponent } from './cwip-transfer-delete-dialog.component';
import { cwipTransferRoute, cwipTransferPopupRoute } from './cwip-transfer.route';

const ENTITY_STATES = [...cwipTransferRoute, ...cwipTransferPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    CwipTransferComponent,
    CwipTransferDetailComponent,
    CwipTransferUpdateComponent,
    CwipTransferDeleteDialogComponent,
    CwipTransferDeletePopupComponent
  ],
  entryComponents: [CwipTransferDeleteDialogComponent]
})
export class FixedAssetServiceCwipTransferModule {}
