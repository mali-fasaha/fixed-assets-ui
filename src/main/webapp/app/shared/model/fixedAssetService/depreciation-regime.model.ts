import { AssetDecayType } from 'app/shared/model/enumerations/asset-decay-type.model';

export interface IDepreciationRegime {
  id?: number;
  assetDecayType?: AssetDecayType;
  depreciationRate?: number;
  description?: string;
}

export class DepreciationRegime implements IDepreciationRegime {
  constructor(public id?: number, public assetDecayType?: AssetDecayType, public depreciationRate?: number, public description?: string) {}
}
