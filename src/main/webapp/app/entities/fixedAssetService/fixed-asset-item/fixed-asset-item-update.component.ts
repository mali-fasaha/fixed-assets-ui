import { Component, OnInit, ElementRef } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IFixedAssetItem, FixedAssetItem } from 'app/shared/model/fixedAssetService/fixed-asset-item.model';
import { FixedAssetItemService } from './fixed-asset-item.service';

@Component({
  selector: 'gha-fixed-asset-item-update',
  templateUrl: './fixed-asset-item-update.component.html'
})
export class FixedAssetItemUpdateComponent implements OnInit {
  isSaving: boolean;
  purchaseDateDp: any;

  editForm = this.fb.group({
    id: [],
    serviceOutletCode: [null, [Validators.required]],
    assetCategoryId: [null, [Validators.required]],
    fixedAssetSerialCode: [null, [Validators.required]],
    fixedAssetDescription: [null, [Validators.required]],
    purchaseDate: [null, [Validators.required]],
    purchaseCost: [null, [Validators.required]],
    purchaseTransactionId: [null, [Validators.required]],
    ownershipDocumentId: [],
    assetPicture: [],
    assetPictureContentType: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected fixedAssetItemService: FixedAssetItemService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ fixedAssetItem }) => {
      this.updateForm(fixedAssetItem);
    });
  }

  updateForm(fixedAssetItem: IFixedAssetItem) {
    this.editForm.patchValue({
      id: fixedAssetItem.id,
      serviceOutletCode: fixedAssetItem.serviceOutletCode,
      assetCategoryId: fixedAssetItem.assetCategoryId,
      fixedAssetSerialCode: fixedAssetItem.fixedAssetSerialCode,
      fixedAssetDescription: fixedAssetItem.fixedAssetDescription,
      purchaseDate: fixedAssetItem.purchaseDate,
      purchaseCost: fixedAssetItem.purchaseCost,
      purchaseTransactionId: fixedAssetItem.purchaseTransactionId,
      ownershipDocumentId: fixedAssetItem.ownershipDocumentId,
      assetPicture: fixedAssetItem.assetPicture,
      assetPictureContentType: fixedAssetItem.assetPictureContentType
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string) {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null
    });
    if (this.elementRef && idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const fixedAssetItem = this.createFromForm();
    if (fixedAssetItem.id !== undefined) {
      this.subscribeToSaveResponse(this.fixedAssetItemService.update(fixedAssetItem));
    } else {
      this.subscribeToSaveResponse(this.fixedAssetItemService.create(fixedAssetItem));
    }
  }

  private createFromForm(): IFixedAssetItem {
    return {
      ...new FixedAssetItem(),
      id: this.editForm.get(['id']).value,
      serviceOutletCode: this.editForm.get(['serviceOutletCode']).value,
      assetCategoryId: this.editForm.get(['assetCategoryId']).value,
      fixedAssetSerialCode: this.editForm.get(['fixedAssetSerialCode']).value,
      fixedAssetDescription: this.editForm.get(['fixedAssetDescription']).value,
      purchaseDate: this.editForm.get(['purchaseDate']).value,
      purchaseCost: this.editForm.get(['purchaseCost']).value,
      purchaseTransactionId: this.editForm.get(['purchaseTransactionId']).value,
      ownershipDocumentId: this.editForm.get(['ownershipDocumentId']).value,
      assetPictureContentType: this.editForm.get(['assetPictureContentType']).value,
      assetPicture: this.editForm.get(['assetPicture']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFixedAssetItem>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
