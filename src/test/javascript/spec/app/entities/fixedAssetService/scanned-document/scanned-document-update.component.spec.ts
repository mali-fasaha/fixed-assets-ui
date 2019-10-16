import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { FixedAssetsTestModule } from '../../../../test.module';
import { ScannedDocumentUpdateComponent } from 'app/entities/fixedAssetService/scanned-document/scanned-document-update.component';
import { ScannedDocumentService } from 'app/entities/fixedAssetService/scanned-document/scanned-document.service';
import { ScannedDocument } from 'app/shared/model/fixedAssetService/scanned-document.model';

describe('Component Tests', () => {
  describe('ScannedDocument Management Update Component', () => {
    let comp: ScannedDocumentUpdateComponent;
    let fixture: ComponentFixture<ScannedDocumentUpdateComponent>;
    let service: ScannedDocumentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [FixedAssetsTestModule],
        declarations: [ScannedDocumentUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ScannedDocumentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ScannedDocumentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ScannedDocumentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ScannedDocument(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new ScannedDocument();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
