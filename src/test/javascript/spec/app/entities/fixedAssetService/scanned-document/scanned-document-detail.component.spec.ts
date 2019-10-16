import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FixedAssetsTestModule } from '../../../../test.module';
import { ScannedDocumentDetailComponent } from 'app/entities/fixedAssetService/scanned-document/scanned-document-detail.component';
import { ScannedDocument } from 'app/shared/model/fixedAssetService/scanned-document.model';

describe('Component Tests', () => {
  describe('ScannedDocument Management Detail Component', () => {
    let comp: ScannedDocumentDetailComponent;
    let fixture: ComponentFixture<ScannedDocumentDetailComponent>;
    const route = ({ data: of({ scannedDocument: new ScannedDocument(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [FixedAssetsTestModule],
        declarations: [ScannedDocumentDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ScannedDocumentDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ScannedDocumentDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.scannedDocument).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
