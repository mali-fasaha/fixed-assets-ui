import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'asset-acquisition',
        loadChildren: () =>
          import('./fixedAssetService/asset-acquisition/asset-acquisition.module').then(m => m.FixedAssetServiceAssetAcquisitionModule)
      },
      {
        path: 'asset-depreciation',
        loadChildren: () =>
          import('./fixedAssetService/asset-depreciation/asset-depreciation.module').then(m => m.FixedAssetServiceAssetDepreciationModule)
      },
      {
        path: 'asset-disposal',
        loadChildren: () =>
          import('./fixedAssetService/asset-disposal/asset-disposal.module').then(m => m.FixedAssetServiceAssetDisposalModule)
      },
      {
        path: 'asset-transaction',
        loadChildren: () =>
          import('./fixedAssetService/asset-transaction/asset-transaction.module').then(m => m.FixedAssetServiceAssetTransactionModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class FixedAssetsEntityModule {}
