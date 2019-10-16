// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  FixedAssetCategoryComponentsPage,
  FixedAssetCategoryDeleteDialog,
  FixedAssetCategoryUpdatePage
} from './fixed-asset-category.page-object';

const expect = chai.expect;

describe('FixedAssetCategory e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let fixedAssetCategoryComponentsPage: FixedAssetCategoryComponentsPage;
  let fixedAssetCategoryUpdatePage: FixedAssetCategoryUpdatePage;
  let fixedAssetCategoryDeleteDialog: FixedAssetCategoryDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load FixedAssetCategories', async () => {
    await navBarPage.goToEntity('fixed-asset-category');
    fixedAssetCategoryComponentsPage = new FixedAssetCategoryComponentsPage();
    await browser.wait(ec.visibilityOf(fixedAssetCategoryComponentsPage.title), 5000);
    expect(await fixedAssetCategoryComponentsPage.getTitle()).to.eq('Fixed Asset Categories');
  });

  it('should load create FixedAssetCategory page', async () => {
    await fixedAssetCategoryComponentsPage.clickOnCreateButton();
    fixedAssetCategoryUpdatePage = new FixedAssetCategoryUpdatePage();
    expect(await fixedAssetCategoryUpdatePage.getPageTitle()).to.eq('Create or edit a Fixed Asset Category');
    await fixedAssetCategoryUpdatePage.cancel();
  });

  it('should create and save FixedAssetCategories', async () => {
    const nbButtonsBeforeCreate = await fixedAssetCategoryComponentsPage.countDeleteButtons();

    await fixedAssetCategoryComponentsPage.clickOnCreateButton();
    await promise.all([
      fixedAssetCategoryUpdatePage.setCategoryNameInput('categoryName'),
      fixedAssetCategoryUpdatePage.setCategoryDescriptionInput('categoryDescription'),
      fixedAssetCategoryUpdatePage.setCategoryAssetCodeInput('categoryAssetCode'),
      fixedAssetCategoryUpdatePage.setCategoryDepreciationCodeInput('categoryDepreciationCode'),
      fixedAssetCategoryUpdatePage.setDepreciationRegimeIdInput('5')
    ]);
    expect(await fixedAssetCategoryUpdatePage.getCategoryNameInput()).to.eq(
      'categoryName',
      'Expected CategoryName value to be equals to categoryName'
    );
    expect(await fixedAssetCategoryUpdatePage.getCategoryDescriptionInput()).to.eq(
      'categoryDescription',
      'Expected CategoryDescription value to be equals to categoryDescription'
    );
    expect(await fixedAssetCategoryUpdatePage.getCategoryAssetCodeInput()).to.eq(
      'categoryAssetCode',
      'Expected CategoryAssetCode value to be equals to categoryAssetCode'
    );
    expect(await fixedAssetCategoryUpdatePage.getCategoryDepreciationCodeInput()).to.eq(
      'categoryDepreciationCode',
      'Expected CategoryDepreciationCode value to be equals to categoryDepreciationCode'
    );
    expect(await fixedAssetCategoryUpdatePage.getDepreciationRegimeIdInput()).to.eq(
      '5',
      'Expected depreciationRegimeId value to be equals to 5'
    );
    await fixedAssetCategoryUpdatePage.save();
    expect(await fixedAssetCategoryUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await fixedAssetCategoryComponentsPage.countDeleteButtons()).to.eq(
      nbButtonsBeforeCreate + 1,
      'Expected one more entry in the table'
    );
  });

  it('should delete last FixedAssetCategory', async () => {
    const nbButtonsBeforeDelete = await fixedAssetCategoryComponentsPage.countDeleteButtons();
    await fixedAssetCategoryComponentsPage.clickOnLastDeleteButton();

    fixedAssetCategoryDeleteDialog = new FixedAssetCategoryDeleteDialog();
    expect(await fixedAssetCategoryDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Fixed Asset Category?');
    await fixedAssetCategoryDeleteDialog.clickOnConfirmButton();

    expect(await fixedAssetCategoryComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
