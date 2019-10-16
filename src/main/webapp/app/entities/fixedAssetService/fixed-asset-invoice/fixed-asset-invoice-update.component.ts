import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IFixedAssetInvoice, FixedAssetInvoice } from 'app/shared/model/fixedAssetService/fixed-asset-invoice.model';
import { FixedAssetInvoiceService } from './fixed-asset-invoice.service';
import { IDealer } from 'app/shared/model/fixedAssetService/dealer.model';
import { DealerService } from 'app/entities/fixedAssetService/dealer/dealer.service';

@Component({
  selector: 'gha-fixed-asset-invoice-update',
  templateUrl: './fixed-asset-invoice-update.component.html'
})
export class FixedAssetInvoiceUpdateComponent implements OnInit {
  isSaving: boolean;

  dealers: IDealer[];
  invoiceDateDp: any;

  editForm = this.fb.group({
    id: [],
    invoiceReference: [null, [Validators.required]],
    invoiceDate: [],
    invoiceAmount: [],
    isProforma: [],
    isCreditNote: [],
    attachments: [],
    attachmentsContentType: [],
    dealerId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected fixedAssetInvoiceService: FixedAssetInvoiceService,
    protected dealerService: DealerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ fixedAssetInvoice }) => {
      this.updateForm(fixedAssetInvoice);
    });
    this.dealerService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IDealer[]>) => mayBeOk.ok),
        map((response: HttpResponse<IDealer[]>) => response.body)
      )
      .subscribe((res: IDealer[]) => (this.dealers = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(fixedAssetInvoice: IFixedAssetInvoice) {
    this.editForm.patchValue({
      id: fixedAssetInvoice.id,
      invoiceReference: fixedAssetInvoice.invoiceReference,
      invoiceDate: fixedAssetInvoice.invoiceDate,
      invoiceAmount: fixedAssetInvoice.invoiceAmount,
      isProforma: fixedAssetInvoice.isProforma,
      isCreditNote: fixedAssetInvoice.isCreditNote,
      attachments: fixedAssetInvoice.attachments,
      attachmentsContentType: fixedAssetInvoice.attachmentsContentType,
      dealerId: fixedAssetInvoice.dealerId
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

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const fixedAssetInvoice = this.createFromForm();
    if (fixedAssetInvoice.id !== undefined) {
      this.subscribeToSaveResponse(this.fixedAssetInvoiceService.update(fixedAssetInvoice));
    } else {
      this.subscribeToSaveResponse(this.fixedAssetInvoiceService.create(fixedAssetInvoice));
    }
  }

  private createFromForm(): IFixedAssetInvoice {
    return {
      ...new FixedAssetInvoice(),
      id: this.editForm.get(['id']).value,
      invoiceReference: this.editForm.get(['invoiceReference']).value,
      invoiceDate: this.editForm.get(['invoiceDate']).value,
      invoiceAmount: this.editForm.get(['invoiceAmount']).value,
      isProforma: this.editForm.get(['isProforma']).value,
      isCreditNote: this.editForm.get(['isCreditNote']).value,
      attachmentsContentType: this.editForm.get(['attachmentsContentType']).value,
      attachments: this.editForm.get(['attachments']).value,
      dealerId: this.editForm.get(['dealerId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFixedAssetInvoice>>) {
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

  trackDealerById(index: number, item: IDealer) {
    return item.id;
  }
}
