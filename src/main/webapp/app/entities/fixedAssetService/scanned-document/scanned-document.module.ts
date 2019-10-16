import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { ScannedDocumentComponent } from './scanned-document.component';
import { ScannedDocumentDetailComponent } from './scanned-document-detail.component';
import { ScannedDocumentUpdateComponent } from './scanned-document-update.component';
import { ScannedDocumentDeletePopupComponent, ScannedDocumentDeleteDialogComponent } from './scanned-document-delete-dialog.component';
import { scannedDocumentRoute, scannedDocumentPopupRoute } from './scanned-document.route';

const ENTITY_STATES = [...scannedDocumentRoute, ...scannedDocumentPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ScannedDocumentComponent,
    ScannedDocumentDetailComponent,
    ScannedDocumentUpdateComponent,
    ScannedDocumentDeleteDialogComponent,
    ScannedDocumentDeletePopupComponent
  ],
  entryComponents: [ScannedDocumentDeleteDialogComponent]
})
export class FixedAssetServiceScannedDocumentModule {}
