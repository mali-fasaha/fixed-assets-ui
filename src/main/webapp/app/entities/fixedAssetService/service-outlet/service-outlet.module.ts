import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { ServiceOutletComponent } from './service-outlet.component';
import { ServiceOutletDetailComponent } from './service-outlet-detail.component';
import { ServiceOutletUpdateComponent } from './service-outlet-update.component';
import { ServiceOutletDeletePopupComponent, ServiceOutletDeleteDialogComponent } from './service-outlet-delete-dialog.component';
import { serviceOutletRoute, serviceOutletPopupRoute } from './service-outlet.route';

const ENTITY_STATES = [...serviceOutletRoute, ...serviceOutletPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ServiceOutletComponent,
    ServiceOutletDetailComponent,
    ServiceOutletUpdateComponent,
    ServiceOutletDeleteDialogComponent,
    ServiceOutletDeletePopupComponent
  ],
  entryComponents: [ServiceOutletDeleteDialogComponent]
})
export class FixedAssetServiceServiceOutletModule {}
