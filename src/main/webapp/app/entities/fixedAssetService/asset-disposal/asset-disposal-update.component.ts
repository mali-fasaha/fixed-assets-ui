import { Component, OnInit, ElementRef } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IAssetDisposal, AssetDisposal } from 'app/shared/model/fixedAssetService/asset-disposal.model';
import { AssetDisposalService } from './asset-disposal.service';

@Component({
  selector: 'gha-asset-disposal-update',
  templateUrl: './asset-disposal-update.component.html'
})
export class AssetDisposalUpdateComponent implements OnInit {
  isSaving: boolean;
  disposalMonthDp: any;

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    disposalMonth: [null, [Validators.required]],
    assetCategoryId: [null, [Validators.required]],
    assetItemId: [null, [Validators.required]],
    disposalProceeds: [null, [Validators.required]],
    netBookValue: [],
    profitOnDisposal: [],
    scannedDocumentId: [],
    assetDealerId: [],
    assetPicture: [],
    assetPictureContentType: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected assetDisposalService: AssetDisposalService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ assetDisposal }) => {
      this.updateForm(assetDisposal);
    });
  }

  updateForm(assetDisposal: IAssetDisposal) {
    this.editForm.patchValue({
      id: assetDisposal.id,
      description: assetDisposal.description,
      disposalMonth: assetDisposal.disposalMonth,
      assetCategoryId: assetDisposal.assetCategoryId,
      assetItemId: assetDisposal.assetItemId,
      disposalProceeds: assetDisposal.disposalProceeds,
      netBookValue: assetDisposal.netBookValue,
      profitOnDisposal: assetDisposal.profitOnDisposal,
      scannedDocumentId: assetDisposal.scannedDocumentId,
      assetDealerId: assetDisposal.assetDealerId,
      assetPicture: assetDisposal.assetPicture,
      assetPictureContentType: assetDisposal.assetPictureContentType
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
    const assetDisposal = this.createFromForm();
    if (assetDisposal.id !== undefined) {
      this.subscribeToSaveResponse(this.assetDisposalService.update(assetDisposal));
    } else {
      this.subscribeToSaveResponse(this.assetDisposalService.create(assetDisposal));
    }
  }

  private createFromForm(): IAssetDisposal {
    return {
      ...new AssetDisposal(),
      id: this.editForm.get(['id']).value,
      description: this.editForm.get(['description']).value,
      disposalMonth: this.editForm.get(['disposalMonth']).value,
      assetCategoryId: this.editForm.get(['assetCategoryId']).value,
      assetItemId: this.editForm.get(['assetItemId']).value,
      disposalProceeds: this.editForm.get(['disposalProceeds']).value,
      netBookValue: this.editForm.get(['netBookValue']).value,
      profitOnDisposal: this.editForm.get(['profitOnDisposal']).value,
      scannedDocumentId: this.editForm.get(['scannedDocumentId']).value,
      assetDealerId: this.editForm.get(['assetDealerId']).value,
      assetPictureContentType: this.editForm.get(['assetPictureContentType']).value,
      assetPicture: this.editForm.get(['assetPicture']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAssetDisposal>>) {
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
