import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { FixedAssetCategoryService } from 'app/entities/fixedAssetService/fixed-asset-category/fixed-asset-category.service';
import { IFixedAssetCategory, FixedAssetCategory } from 'app/shared/model/fixedAssetService/fixed-asset-category.model';

describe('Service Tests', () => {
  describe('FixedAssetCategory Service', () => {
    let injector: TestBed;
    let service: FixedAssetCategoryService;
    let httpMock: HttpTestingController;
    let elemDefault: IFixedAssetCategory;
    let expectedResult;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(FixedAssetCategoryService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new FixedAssetCategory(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0);
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

      it('should create a FixedAssetCategory', () => {
        const returnedFromService = Object.assign(
          {
            id: 0
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .create(new FixedAssetCategory(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a FixedAssetCategory', () => {
        const returnedFromService = Object.assign(
          {
            categoryName: 'BBBBBB',
            categoryDescription: 'BBBBBB',
            categoryAssetCode: 'BBBBBB',
            categoryDepreciationCode: 'BBBBBB',
            depreciationRegimeId: 1
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

      it('should return a list of FixedAssetCategory', () => {
        const returnedFromService = Object.assign(
          {
            categoryName: 'BBBBBB',
            categoryDescription: 'BBBBBB',
            categoryAssetCode: 'BBBBBB',
            categoryDepreciationCode: 'BBBBBB',
            depreciationRegimeId: 1
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

      it('should delete a FixedAssetCategory', () => {
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
