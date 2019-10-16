import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ITransactionApproval, TransactionApproval } from 'app/shared/model/fixedAssetService/transaction-approval.model';
import { TransactionApprovalService } from './transaction-approval.service';

@Component({
  selector: 'gha-transaction-approval-update',
  templateUrl: './transaction-approval-update.component.html'
})
export class TransactionApprovalUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    description: [],
    requestedBy: [],
    recommendedBy: [null, []],
    reviewedBy: [null, []],
    firstApprover: [null, []],
    secondApprover: [null, []],
    thirdApprover: [null, []],
    fourthApprover: [null, []]
  });

  constructor(
    protected transactionApprovalService: TransactionApprovalService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ transactionApproval }) => {
      this.updateForm(transactionApproval);
    });
  }

  updateForm(transactionApproval: ITransactionApproval) {
    this.editForm.patchValue({
      id: transactionApproval.id,
      description: transactionApproval.description,
      requestedBy: transactionApproval.requestedBy,
      recommendedBy: transactionApproval.recommendedBy,
      reviewedBy: transactionApproval.reviewedBy,
      firstApprover: transactionApproval.firstApprover,
      secondApprover: transactionApproval.secondApprover,
      thirdApprover: transactionApproval.thirdApprover,
      fourthApprover: transactionApproval.fourthApprover
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const transactionApproval = this.createFromForm();
    if (transactionApproval.id !== undefined) {
      this.subscribeToSaveResponse(this.transactionApprovalService.update(transactionApproval));
    } else {
      this.subscribeToSaveResponse(this.transactionApprovalService.create(transactionApproval));
    }
  }

  private createFromForm(): ITransactionApproval {
    return {
      ...new TransactionApproval(),
      id: this.editForm.get(['id']).value,
      description: this.editForm.get(['description']).value,
      requestedBy: this.editForm.get(['requestedBy']).value,
      recommendedBy: this.editForm.get(['recommendedBy']).value,
      reviewedBy: this.editForm.get(['reviewedBy']).value,
      firstApprover: this.editForm.get(['firstApprover']).value,
      secondApprover: this.editForm.get(['secondApprover']).value,
      thirdApprover: this.editForm.get(['thirdApprover']).value,
      fourthApprover: this.editForm.get(['fourthApprover']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransactionApproval>>) {
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
