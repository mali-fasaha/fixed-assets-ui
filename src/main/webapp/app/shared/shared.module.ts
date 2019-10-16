import { NgModule } from '@angular/core';
import { FixedAssetsSharedLibsModule } from './shared-libs.module';
import { GhaAlertComponent } from './alert/alert.component';
import { GhaAlertErrorComponent } from './alert/alert-error.component';
import { GhaLoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';

@NgModule({
  imports: [FixedAssetsSharedLibsModule],
  declarations: [GhaAlertComponent, GhaAlertErrorComponent, GhaLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [GhaLoginModalComponent],
  exports: [FixedAssetsSharedLibsModule, GhaAlertComponent, GhaAlertErrorComponent, GhaLoginModalComponent, HasAnyAuthorityDirective]
})
export class FixedAssetsSharedModule {}
