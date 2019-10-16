import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FileTypeComponent } from './file-type.component';
import { FileTypeDetailComponent } from './file-type-detail.component';
import { FileTypeUpdateComponent } from './file-type-update.component';
import { FileTypeDeletePopupComponent, FileTypeDeleteDialogComponent } from './file-type-delete-dialog.component';
import { fileTypeRoute, fileTypePopupRoute } from './file-type.route';

const ENTITY_STATES = [...fileTypeRoute, ...fileTypePopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FileTypeComponent,
    FileTypeDetailComponent,
    FileTypeUpdateComponent,
    FileTypeDeleteDialogComponent,
    FileTypeDeletePopupComponent
  ],
  entryComponents: [FileTypeDeleteDialogComponent]
})
export class FixedAssetServiceFileTypeModule {}
