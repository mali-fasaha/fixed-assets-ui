import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FixedAssetCategoryComponent } from './fixed-asset-category.component';
import { FixedAssetCategoryDetailComponent } from './fixed-asset-category-detail.component';
import { FixedAssetCategoryUpdateComponent } from './fixed-asset-category-update.component';
import {
  FixedAssetCategoryDeletePopupComponent,
  FixedAssetCategoryDeleteDialogComponent
} from './fixed-asset-category-delete-dialog.component';
import { fixedAssetCategoryRoute, fixedAssetCategoryPopupRoute } from './fixed-asset-category.route';

const ENTITY_STATES = [...fixedAssetCategoryRoute, ...fixedAssetCategoryPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FixedAssetCategoryComponent,
    FixedAssetCategoryDetailComponent,
    FixedAssetCategoryUpdateComponent,
    FixedAssetCategoryDeleteDialogComponent,
    FixedAssetCategoryDeletePopupComponent
  ],
  entryComponents: [FixedAssetCategoryDeleteDialogComponent]
})
export class FixedAssetServiceFixedAssetCategoryModule {}
