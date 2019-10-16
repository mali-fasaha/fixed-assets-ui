import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAssetTransaction } from 'app/shared/model/fixedAssetService/asset-transaction.model';

@Component({
  selector: 'gha-asset-transaction-detail',
  templateUrl: './asset-transaction-detail.component.html'
})
export class AssetTransactionDetailComponent implements OnInit {
  assetTransaction: IAssetTransaction;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ assetTransaction }) => {
      this.assetTransaction = assetTransaction;
    });
  }

  previousState() {
    window.history.back();
  }
}
