import { Route } from '@angular/router';

import { GhaDocsComponent } from './docs.component';

export const docsRoute: Route = {
  path: '',
  component: GhaDocsComponent,
  data: {
    pageTitle: 'API'
  }
};
