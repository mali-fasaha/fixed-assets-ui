import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { EmployeeComponent } from './employee.component';
import { EmployeeDetailComponent } from './employee-detail.component';
import { EmployeeUpdateComponent } from './employee-update.component';
import { EmployeeDeletePopupComponent, EmployeeDeleteDialogComponent } from './employee-delete-dialog.component';
import { employeeRoute, employeePopupRoute } from './employee.route';

const ENTITY_STATES = [...employeeRoute, ...employeePopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    EmployeeComponent,
    EmployeeDetailComponent,
    EmployeeUpdateComponent,
    EmployeeDeleteDialogComponent,
    EmployeeDeletePopupComponent
  ],
  entryComponents: [EmployeeDeleteDialogComponent]
})
export class FixedAssetServiceEmployeeModule {}
