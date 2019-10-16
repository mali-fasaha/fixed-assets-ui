import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaConfigurationComponent } from './configuration.component';

import { configurationRoute } from './configuration.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([configurationRoute])],
  declarations: [GhaConfigurationComponent]
})
export class ConfigurationModule {}
