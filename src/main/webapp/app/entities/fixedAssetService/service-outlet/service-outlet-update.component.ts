import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IServiceOutlet, ServiceOutlet } from 'app/shared/model/fixedAssetService/service-outlet.model';
import { ServiceOutletService } from './service-outlet.service';

@Component({
  selector: 'gha-service-outlet-update',
  templateUrl: './service-outlet-update.component.html'
})
export class ServiceOutletUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    serviceOutletCode: [null, [Validators.required]],
    serviceOutletDesignation: [null, [Validators.required]],
    description: [],
    location: []
  });

  constructor(protected serviceOutletService: ServiceOutletService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ serviceOutlet }) => {
      this.updateForm(serviceOutlet);
    });
  }

  updateForm(serviceOutlet: IServiceOutlet) {
    this.editForm.patchValue({
      id: serviceOutlet.id,
      serviceOutletCode: serviceOutlet.serviceOutletCode,
      serviceOutletDesignation: serviceOutlet.serviceOutletDesignation,
      description: serviceOutlet.description,
      location: serviceOutlet.location
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const serviceOutlet = this.createFromForm();
    if (serviceOutlet.id !== undefined) {
      this.subscribeToSaveResponse(this.serviceOutletService.update(serviceOutlet));
    } else {
      this.subscribeToSaveResponse(this.serviceOutletService.create(serviceOutlet));
    }
  }

  private createFromForm(): IServiceOutlet {
    return {
      ...new ServiceOutlet(),
      id: this.editForm.get(['id']).value,
      serviceOutletCode: this.editForm.get(['serviceOutletCode']).value,
      serviceOutletDesignation: this.editForm.get(['serviceOutletDesignation']).value,
      description: this.editForm.get(['description']).value,
      location: this.editForm.get(['location']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServiceOutlet>>) {
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
