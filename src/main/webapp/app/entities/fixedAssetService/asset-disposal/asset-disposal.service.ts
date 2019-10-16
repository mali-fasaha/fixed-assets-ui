import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IAssetDisposal } from 'app/shared/model/fixedAssetService/asset-disposal.model';

type EntityResponseType = HttpResponse<IAssetDisposal>;
type EntityArrayResponseType = HttpResponse<IAssetDisposal[]>;

@Injectable({ providedIn: 'root' })
export class AssetDisposalService {
  public resourceUrl = SERVER_API_URL + 'services/fixedassetservice/api/asset-disposals';
  public resourceSearchUrl = SERVER_API_URL + 'services/fixedassetservice/api/_search/asset-disposals';

  constructor(protected http: HttpClient) {}

  create(assetDisposal: IAssetDisposal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(assetDisposal);
    return this.http
      .post<IAssetDisposal>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(assetDisposal: IAssetDisposal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(assetDisposal);
    return this.http
      .put<IAssetDisposal>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAssetDisposal>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAssetDisposal[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAssetDisposal[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(assetDisposal: IAssetDisposal): IAssetDisposal {
    const copy: IAssetDisposal = Object.assign({}, assetDisposal, {
      disposalMonth:
        assetDisposal.disposalMonth != null && assetDisposal.disposalMonth.isValid()
          ? assetDisposal.disposalMonth.format(DATE_FORMAT)
          : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.disposalMonth = res.body.disposalMonth != null ? moment(res.body.disposalMonth) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((assetDisposal: IAssetDisposal) => {
        assetDisposal.disposalMonth = assetDisposal.disposalMonth != null ? moment(assetDisposal.disposalMonth) : null;
      });
    }
    return res;
  }
}
