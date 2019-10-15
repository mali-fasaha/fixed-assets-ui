import { Route } from '@angular/router';

import { GhaTrackerComponent } from './tracker.component';

export const trackerRoute: Route = {
  path: '',
  component: GhaTrackerComponent,
  data: {
    pageTitle: 'Real-time user activities'
  }
};
