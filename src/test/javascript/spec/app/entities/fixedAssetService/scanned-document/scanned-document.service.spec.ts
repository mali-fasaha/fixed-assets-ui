import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { ScannedDocumentService } from 'app/entities/fixedAssetService/scanned-document/scanned-document.service';
import { IScannedDocument, ScannedDocument } from 'app/shared/model/fixedAssetService/scanned-document.model';

describe('Service Tests', () => {
  describe('ScannedDocument Service', () => {
    let injector: TestBed;
    let service: ScannedDocumentService;
    let httpMock: HttpTestingController;
    let elemDefault: IScannedDocument;
    let expectedResult;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(ScannedDocumentService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new ScannedDocument(
        0,
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA'
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a ScannedDocument', () => {
        const returnedFromService = Object.assign(
          {
            id: 0
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .create(new ScannedDocument(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a ScannedDocument', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            approvalDocument: 'BBBBBB',
            invoiceDocument: 'BBBBBB',
            lpoDocument: 'BBBBBB',
            requisitionDocument: 'BBBBBB',
            otherDocuments: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of ScannedDocument', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            approvalDocument: 'BBBBBB',
            invoiceDocument: 'BBBBBB',
            lpoDocument: 'BBBBBB',
            requisitionDocument: 'BBBBBB',
            otherDocuments: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a ScannedDocument', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
