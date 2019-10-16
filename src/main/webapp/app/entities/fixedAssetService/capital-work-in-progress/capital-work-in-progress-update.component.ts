import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { ICapitalWorkInProgress, CapitalWorkInProgress } from 'app/shared/model/fixedAssetService/capital-work-in-progress.model';
import { CapitalWorkInProgressService } from './capital-work-in-progress.service';

@Component({
  selector: 'gha-capital-work-in-progress-update',
  templateUrl: './capital-work-in-progress-update.component.html'
})
export class CapitalWorkInProgressUpdateComponent implements OnInit {
  isSaving: boolean;
  transactionMonthDp: any;

  editForm = this.fb.group({
    id: [],
    transactionMonth: [null, [Validators.required]],
    assetSerialTag: [null, [Validators.required]],
    serviceOutletCode: [null, [Validators.required]],
    transactionId: [null, [Validators.required]],
    transactionDetails: [null, [Validators.required]],
    transactionAmount: [null, [Validators.required]]
  });

  constructor(
    protected capitalWorkInProgressService: CapitalWorkInProgressService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ capitalWorkInProgress }) => {
      this.updateForm(capitalWorkInProgress);
    });
  }

  updateForm(capitalWorkInProgress: ICapitalWorkInProgress) {
    this.editForm.patchValue({
      id: capitalWorkInProgress.id,
      transactionMonth: capitalWorkInProgress.transactionMonth,
      assetSerialTag: capitalWorkInProgress.assetSerialTag,
      serviceOutletCode: capitalWorkInProgress.serviceOutletCode,
      transactionId: capitalWorkInProgress.transactionId,
      transactionDetails: capitalWorkInProgress.transactionDetails,
      transactionAmount: capitalWorkInProgress.transactionAmount
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const capitalWorkInProgress = this.createFromForm();
    if (capitalWorkInProgress.id !== undefined) {
      this.subscribeToSaveResponse(this.capitalWorkInProgressService.update(capitalWorkInProgress));
    } else {
      this.subscribeToSaveResponse(this.capitalWorkInProgressService.create(capitalWorkInProgress));
    }
  }

  private createFromForm(): ICapitalWorkInProgress {
    return {
      ...new CapitalWorkInProgress(),
      id: this.editForm.get(['id']).value,
      transactionMonth: this.editForm.get(['transactionMonth']).value,
      assetSerialTag: this.editForm.get(['assetSerialTag']).value,
      serviceOutletCode: this.editForm.get(['serviceOutletCode']).value,
      transactionId: this.editForm.get(['transactionId']).value,
      transactionDetails: this.editForm.get(['transactionDetails']).value,
      transactionAmount: this.editForm.get(['transactionAmount']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICapitalWorkInProgress>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
