import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { FixedAssetsSharedModule } from 'app/shared/shared.module';
import { FixedAssetsCoreModule } from 'app/core/core.module';
import { FixedAssetsAppRoutingModule } from './app-routing.module';
import { FixedAssetsHomeModule } from './home/home.module';
import { FixedAssetsEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { GhaMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    FixedAssetsSharedModule,
    FixedAssetsCoreModule,
    FixedAssetsHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    FixedAssetsEntityModule,
    FixedAssetsAppRoutingModule
  ],
  declarations: [GhaMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [GhaMainComponent]
})
export class FixedAssetsAppModule {}
