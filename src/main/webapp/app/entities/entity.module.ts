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
      },
      {
        path: 'capital-work-in-progress',
        loadChildren: () =>
          import('./fixedAssetService/capital-work-in-progress/capital-work-in-progress.module').then(
            m => m.FixedAssetServiceCapitalWorkInProgressModule
          )
      },
      {
        path: 'cwip-transfer',
        loadChildren: () =>
          import('./fixedAssetService/cwip-transfer/cwip-transfer.module').then(m => m.FixedAssetServiceCwipTransferModule)
      },
      {
        path: 'dealer',
        loadChildren: () => import('./fixedAssetService/dealer/dealer.module').then(m => m.FixedAssetServiceDealerModule)
      },
      {
        path: 'fixed-asset-invoice',
        loadChildren: () =>
          import('./fixedAssetService/fixed-asset-invoice/fixed-asset-invoice.module').then(m => m.FixedAssetServiceFixedAssetInvoiceModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class FixedAssetsEntityModule {}
