import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IFixedAssetCategory, FixedAssetCategory } from 'app/shared/model/fixedAssetService/fixed-asset-category.model';
import { FixedAssetCategoryService } from './fixed-asset-category.service';

@Component({
  selector: 'gha-fixed-asset-category-update',
  templateUrl: './fixed-asset-category-update.component.html'
})
export class FixedAssetCategoryUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    categoryName: [null, [Validators.required]],
    categoryDescription: [],
    categoryAssetCode: [null, [Validators.required]],
    categoryDepreciationCode: [null, [Validators.required]],
    depreciationRegimeId: []
  });

  constructor(
    protected fixedAssetCategoryService: FixedAssetCategoryService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ fixedAssetCategory }) => {
      this.updateForm(fixedAssetCategory);
    });
  }

  updateForm(fixedAssetCategory: IFixedAssetCategory) {
    this.editForm.patchValue({
      id: fixedAssetCategory.id,
      categoryName: fixedAssetCategory.categoryName,
      categoryDescription: fixedAssetCategory.categoryDescription,
      categoryAssetCode: fixedAssetCategory.categoryAssetCode,
      categoryDepreciationCode: fixedAssetCategory.categoryDepreciationCode,
      depreciationRegimeId: fixedAssetCategory.depreciationRegimeId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const fixedAssetCategory = this.createFromForm();
    if (fixedAssetCategory.id !== undefined) {
      this.subscribeToSaveResponse(this.fixedAssetCategoryService.update(fixedAssetCategory));
    } else {
      this.subscribeToSaveResponse(this.fixedAssetCategoryService.create(fixedAssetCategory));
    }
  }

  private createFromForm(): IFixedAssetCategory {
    return {
      ...new FixedAssetCategory(),
      id: this.editForm.get(['id']).value,
      categoryName: this.editForm.get(['categoryName']).value,
      categoryDescription: this.editForm.get(['categoryDescription']).value,
      categoryAssetCode: this.editForm.get(['categoryAssetCode']).value,
      categoryDepreciationCode: this.editForm.get(['categoryDepreciationCode']).value,
      depreciationRegimeId: this.editForm.get(['depreciationRegimeId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFixedAssetCategory>>) {
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
