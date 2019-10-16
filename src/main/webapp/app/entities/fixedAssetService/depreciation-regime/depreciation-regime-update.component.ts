import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IDepreciationRegime, DepreciationRegime } from 'app/shared/model/fixedAssetService/depreciation-regime.model';
import { DepreciationRegimeService } from './depreciation-regime.service';

@Component({
  selector: 'gha-depreciation-regime-update',
  templateUrl: './depreciation-regime-update.component.html'
})
export class DepreciationRegimeUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    assetDecayType: [null, [Validators.required]],
    depreciationRate: [null, [Validators.required]],
    description: []
  });

  constructor(
    protected depreciationRegimeService: DepreciationRegimeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ depreciationRegime }) => {
      this.updateForm(depreciationRegime);
    });
  }

  updateForm(depreciationRegime: IDepreciationRegime) {
    this.editForm.patchValue({
      id: depreciationRegime.id,
      assetDecayType: depreciationRegime.assetDecayType,
      depreciationRate: depreciationRegime.depreciationRate,
      description: depreciationRegime.description
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const depreciationRegime = this.createFromForm();
    if (depreciationRegime.id !== undefined) {
      this.subscribeToSaveResponse(this.depreciationRegimeService.update(depreciationRegime));
    } else {
      this.subscribeToSaveResponse(this.depreciationRegimeService.create(depreciationRegime));
    }
  }

  private createFromForm(): IDepreciationRegime {
    return {
      ...new DepreciationRegime(),
      id: this.editForm.get(['id']).value,
      assetDecayType: this.editForm.get(['assetDecayType']).value,
      depreciationRate: this.editForm.get(['depreciationRate']).value,
      description: this.editForm.get(['description']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDepreciationRegime>>) {
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
