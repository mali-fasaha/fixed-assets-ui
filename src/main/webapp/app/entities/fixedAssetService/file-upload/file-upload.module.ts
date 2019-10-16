import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FileUploadComponent } from './file-upload.component';
import { FileUploadDetailComponent } from './file-upload-detail.component';
import { FileUploadUpdateComponent } from './file-upload-update.component';
import { FileUploadDeletePopupComponent, FileUploadDeleteDialogComponent } from './file-upload-delete-dialog.component';
import { fileUploadRoute, fileUploadPopupRoute } from './file-upload.route';

const ENTITY_STATES = [...fileUploadRoute, ...fileUploadPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FileUploadComponent,
    FileUploadDetailComponent,
    FileUploadUpdateComponent,
    FileUploadDeleteDialogComponent,
    FileUploadDeletePopupComponent
  ],
  entryComponents: [FileUploadDeleteDialogComponent]
})
export class FixedAssetServiceFileUploadModule {}
