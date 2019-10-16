import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { FixedAssetItemService } from 'app/entities/fixedAssetService/fixed-asset-item/fixed-asset-item.service';
import { IFixedAssetItem, FixedAssetItem } from 'app/shared/model/fixedAssetService/fixed-asset-item.model';

describe('Service Tests', () => {
  describe('FixedAssetItem Service', () => {
    let injector: TestBed;
    let service: FixedAssetItemService;
    let httpMock: HttpTestingController;
    let elemDefault: IFixedAssetItem;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(FixedAssetItemService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new FixedAssetItem(0, 'AAAAAAA', 0, 'AAAAAAA', 'AAAAAAA', currentDate, 0, 0, 0, 'image/png', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            purchaseDate: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a FixedAssetItem', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            purchaseDate: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            purchaseDate: currentDate
          },
          returnedFromService
        );
        service
          .create(new FixedAssetItem(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a FixedAssetItem', () => {
        const returnedFromService = Object.assign(
          {
            serviceOutletCode: 'BBBBBB',
            assetCategoryId: 1,
            fixedAssetSerialCode: 'BBBBBB',
            fixedAssetDescription: 'BBBBBB',
            purchaseDate: currentDate.format(DATE_FORMAT),
            purchaseCost: 1,
            purchaseTransactionId: 1,
            ownershipDocumentId: 1,
            assetPicture: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            purchaseDate: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of FixedAssetItem', () => {
        const returnedFromService = Object.assign(
          {
            serviceOutletCode: 'BBBBBB',
            assetCategoryId: 1,
            fixedAssetSerialCode: 'BBBBBB',
            fixedAssetDescription: 'BBBBBB',
            purchaseDate: currentDate.format(DATE_FORMAT),
            purchaseCost: 1,
            purchaseTransactionId: 1,
            ownershipDocumentId: 1,
            assetPicture: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            purchaseDate: currentDate
          },
          returnedFromService
        );
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

      it('should delete a FixedAssetItem', () => {
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
