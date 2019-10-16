import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { TransactionApprovalComponent } from './transaction-approval.component';
import { TransactionApprovalDetailComponent } from './transaction-approval-detail.component';
import { TransactionApprovalUpdateComponent } from './transaction-approval-update.component';
import {
  TransactionApprovalDeletePopupComponent,
  TransactionApprovalDeleteDialogComponent
} from './transaction-approval-delete-dialog.component';
import { transactionApprovalRoute, transactionApprovalPopupRoute } from './transaction-approval.route';

const ENTITY_STATES = [...transactionApprovalRoute, ...transactionApprovalPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    TransactionApprovalComponent,
    TransactionApprovalDetailComponent,
    TransactionApprovalUpdateComponent,
    TransactionApprovalDeleteDialogComponent,
    TransactionApprovalDeletePopupComponent
  ],
  entryComponents: [TransactionApprovalDeleteDialogComponent]
})
export class FixedAssetServiceTransactionApprovalModule {}
