import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FixedAssetAssessmentComponent } from './fixed-asset-assessment.component';
import { FixedAssetAssessmentDetailComponent } from './fixed-asset-assessment-detail.component';
import { FixedAssetAssessmentUpdateComponent } from './fixed-asset-assessment-update.component';
import {
  FixedAssetAssessmentDeletePopupComponent,
  FixedAssetAssessmentDeleteDialogComponent
} from './fixed-asset-assessment-delete-dialog.component';
import { fixedAssetAssessmentRoute, fixedAssetAssessmentPopupRoute } from './fixed-asset-assessment.route';

const ENTITY_STATES = [...fixedAssetAssessmentRoute, ...fixedAssetAssessmentPopupRoute];

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    FixedAssetAssessmentComponent,
    FixedAssetAssessmentDetailComponent,
    FixedAssetAssessmentUpdateComponent,
    FixedAssetAssessmentDeleteDialogComponent,
    FixedAssetAssessmentDeletePopupComponent
  ],
  entryComponents: [FixedAssetAssessmentDeleteDialogComponent]
})
export class FixedAssetServiceFixedAssetAssessmentModule {}
