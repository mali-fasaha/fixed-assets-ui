import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FixedAssetItemComponent } from './fixed-asset-item.component';
import { FixedAssetItemDetailComponent } from './fixed-asset-item-detail.component';
import { FixedAssetItemUpdateComponent } from './fixed-asset-item-update.component';
import { FixedAssetItemDeletePopupComponent, FixedAssetItemDeleteDialogComponent } from './fixed-asset-item-delete-dialog.component';
import { fixedAssetItemRoute, fixedAssetItemPopupRoute } from './fixed-asset-item.route';

const ENTITY_STATES = [...fixedAssetItemRoute, ...fixedAssetItemPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FixedAssetItemComponent,
    FixedAssetItemDetailComponent,
    FixedAssetItemUpdateComponent,
    FixedAssetItemDeleteDialogComponent,
    FixedAssetItemDeletePopupComponent
  ],
  entryComponents: [FixedAssetItemDeleteDialogComponent]
})
export class FixedAssetServiceFixedAssetItemModule {}
