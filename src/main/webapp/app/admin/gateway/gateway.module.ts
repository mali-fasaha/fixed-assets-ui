import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';

import { GhaGatewayComponent } from './gateway.component';

import { gatewayRoute } from './gateway.route';

@NgModule({
  imports: [FixedAssetsSharedModule, RouterModule.forChild([gatewayRoute])],
  declarations: [GhaGatewayComponent]
})
export class GatewayModule {}
