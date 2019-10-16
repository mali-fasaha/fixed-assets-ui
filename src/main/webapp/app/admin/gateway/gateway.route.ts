import { Route } from '@angular/router';

import { GhaGatewayComponent } from './gateway.component';

export const gatewayRoute: Route = {
  path: '',
  component: GhaGatewayComponent,
  data: {
    pageTitle: 'Gateway'
  }
};
