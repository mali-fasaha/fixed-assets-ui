import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { TransactionApprovalService } from 'app/entities/fixedAssetService/transaction-approval/transaction-approval.service';
import { ITransactionApproval, TransactionApproval } from 'app/shared/model/fixedAssetService/transaction-approval.model';

describe('Service Tests', () => {
  describe('TransactionApproval Service', () => {
    let injector: TestBed;
    let service: TransactionApprovalService;
    let httpMock: HttpTestingController;
    let elemDefault: ITransactionApproval;
    let expectedResult;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(TransactionApprovalService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new TransactionApproval(0, 'AAAAAAA', 0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
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

      it('should create a TransactionApproval', () => {
        const returnedFromService = Object.assign(
          {
            id: 0
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .create(new TransactionApproval(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a TransactionApproval', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            requestedBy: 1,
            recommendedBy: 'BBBBBB',
            reviewedBy: 'BBBBBB',
            firstApprover: 'BBBBBB',
            secondApprover: 'BBBBBB',
            thirdApprover: 'BBBBBB',
            fourthApprover: 'BBBBBB'
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

      it('should return a list of TransactionApproval', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            requestedBy: 1,
            recommendedBy: 'BBBBBB',
            reviewedBy: 'BBBBBB',
            firstApprover: 'BBBBBB',
            secondApprover: 'BBBBBB',
            thirdApprover: 'BBBBBB',
            fourthApprover: 'BBBBBB'
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

      it('should delete a TransactionApproval', () => {
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
