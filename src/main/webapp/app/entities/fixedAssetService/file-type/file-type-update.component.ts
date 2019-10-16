import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IFileType, FileType } from 'app/shared/model/fixedAssetService/file-type.model';
import { FileTypeService } from './file-type.service';

@Component({
  selector: 'gha-file-type-update',
  templateUrl: './file-type-update.component.html'
})
export class FileTypeUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    fileTypeName: [null, [Validators.required]],
    fileMediumType: [null, [Validators.required]],
    description: [],
    fileTemplate: [],
    fileTemplateContentType: [],
    fileType: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected fileTypeService: FileTypeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ fileType }) => {
      this.updateForm(fileType);
    });
  }

  updateForm(fileType: IFileType) {
    this.editForm.patchValue({
      id: fileType.id,
      fileTypeName: fileType.fileTypeName,
      fileMediumType: fileType.fileMediumType,
      description: fileType.description,
      fileTemplate: fileType.fileTemplate,
      fileTemplateContentType: fileType.fileTemplateContentType,
      fileType: fileType.fileType
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
    const fileType = this.createFromForm();
    if (fileType.id !== undefined) {
      this.subscribeToSaveResponse(this.fileTypeService.update(fileType));
    } else {
      this.subscribeToSaveResponse(this.fileTypeService.create(fileType));
    }
  }

  private createFromForm(): IFileType {
    return {
      ...new FileType(),
      id: this.editForm.get(['id']).value,
      fileTypeName: this.editForm.get(['fileTypeName']).value,
      fileMediumType: this.editForm.get(['fileMediumType']).value,
      description: this.editForm.get(['description']).value,
      fileTemplateContentType: this.editForm.get(['fileTemplateContentType']).value,
      fileTemplate: this.editForm.get(['fileTemplate']).value,
      fileType: this.editForm.get(['fileType']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFileType>>) {
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
