import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaDocsComponent } from './docs.component';

import { docsRoute } from './docs.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([docsRoute])],
  declarations: [GhaDocsComponent]
})
export class DocsModule {}
