package io.github.assets.web.rest;

import io.github.assets.FixedAssetServiceApp;
import io.github.assets.config.SecurityBeanOverrideConfiguration;
import io.github.assets.domain.AssetDepreciation;
import io.github.assets.repository.AssetDepreciationRepository;
import io.github.assets.repository.search.AssetDepreciationSearchRepository;
import io.github.assets.service.AssetDepreciationService;
import io.github.assets.service.dto.AssetDepreciationDTO;
import io.github.assets.service.mapper.AssetDepreciationMapper;
import io.github.assets.web.rest.errors.ExceptionTranslator;
import io.github.assets.service.dto.AssetDepreciationCriteria;
import io.github.assets.service.AssetDepreciationQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static io.github.assets.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AssetDepreciationResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FixedAssetServiceApp.class})
public class AssetDepreciationResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DEPRECIATION_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DEPRECIATION_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DEPRECIATION_AMOUNT = new BigDecimal(1 - 1);

    private static final LocalDate DEFAULT_DEPRECIATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEPRECIATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DEPRECIATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final Long UPDATED_CATEGORY_ID = 2L;
    private static final Long SMALLER_CATEGORY_ID = 1L - 1L;

    private static final Long DEFAULT_ASSET_ITEM_ID = 1L;
    private static final Long UPDATED_ASSET_ITEM_ID = 2L;
    private static final Long SMALLER_ASSET_ITEM_ID = 1L - 1L;

    @Autowired
    private AssetDepreciationRepository assetDepreciationRepository;

    @Autowired
    private AssetDepreciationMapper assetDepreciationMapper;

    @Autowired
    private AssetDepreciationService assetDepreciationService;

    /**
     * This repository is mocked in the io.github.assets.repository.search test package.
     *
     * @see io.github.assets.repository.search.AssetDepreciationSearchRepositoryMockConfiguration
     */
    @Autowired
    private AssetDepreciationSearchRepository mockAssetDepreciationSearchRepository;

    @Autowired
    private AssetDepreciationQueryService assetDepreciationQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restAssetDepreciationMockMvc;

    private AssetDepreciation assetDepreciation;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AssetDepreciationResource assetDepreciationResource = new AssetDepreciationResource(assetDepreciationService, assetDepreciationQueryService);
        this.restAssetDepreciationMockMvc = MockMvcBuilders.standaloneSetup(assetDepreciationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetDepreciation createEntity(EntityManager em) {
        AssetDepreciation assetDepreciation = new AssetDepreciation()
            .description(DEFAULT_DESCRIPTION)
            .depreciationAmount(DEFAULT_DEPRECIATION_AMOUNT)
            .depreciationDate(DEFAULT_DEPRECIATION_DATE)
            .categoryId(DEFAULT_CATEGORY_ID)
            .assetItemId(DEFAULT_ASSET_ITEM_ID);
        return assetDepreciation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetDepreciation createUpdatedEntity(EntityManager em) {
        AssetDepreciation assetDepreciation = new AssetDepreciation()
            .description(UPDATED_DESCRIPTION)
            .depreciationAmount(UPDATED_DEPRECIATION_AMOUNT)
            .depreciationDate(UPDATED_DEPRECIATION_DATE)
            .categoryId(UPDATED_CATEGORY_ID)
            .assetItemId(UPDATED_ASSET_ITEM_ID);
        return assetDepreciation;
    }

    @BeforeEach
    public void initTest() {
        assetDepreciation = createEntity(em);
    }

    @Test
    @Transactional
    public void createAssetDepreciation() throws Exception {
        int databaseSizeBeforeCreate = assetDepreciationRepository.findAll().size();

        // Create the AssetDepreciation
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(assetDepreciation);
        restAssetDepreciationMockMvc.perform(post("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isCreated());

        // Validate the AssetDepreciation in the database
        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeCreate + 1);
        AssetDepreciation testAssetDepreciation = assetDepreciationList.get(assetDepreciationList.size() - 1);
        assertThat(testAssetDepreciation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAssetDepreciation.getDepreciationAmount()).isEqualTo(DEFAULT_DEPRECIATION_AMOUNT);
        assertThat(testAssetDepreciation.getDepreciationDate()).isEqualTo(DEFAULT_DEPRECIATION_DATE);
        assertThat(testAssetDepreciation.getCategoryId()).isEqualTo(DEFAULT_CATEGORY_ID);
        assertThat(testAssetDepreciation.getAssetItemId()).isEqualTo(DEFAULT_ASSET_ITEM_ID);

        // Validate the AssetDepreciation in Elasticsearch
        verify(mockAssetDepreciationSearchRepository, times(1)).save(testAssetDepreciation);
    }

    @Test
    @Transactional
    public void createAssetDepreciationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = assetDepreciationRepository.findAll().size();

        // Create the AssetDepreciation with an existing ID
        assetDepreciation.setId(1L);
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(assetDepreciation);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetDepreciationMockMvc.perform(post("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetDepreciation in the database
        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeCreate);

        // Validate the AssetDepreciation in Elasticsearch
        verify(mockAssetDepreciationSearchRepository, times(0)).save(assetDepreciation);
    }


    @Test
    @Transactional
    public void checkDepreciationAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetDepreciationRepository.findAll().size();
        // set the field null
        assetDepreciation.setDepreciationAmount(null);

        // Create the AssetDepreciation, which fails.
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(assetDepreciation);

        restAssetDepreciationMockMvc.perform(post("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isBadRequest());

        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDepreciationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetDepreciationRepository.findAll().size();
        // set the field null
        assetDepreciation.setDepreciationDate(null);

        // Create the AssetDepreciation, which fails.
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(assetDepreciation);

        restAssetDepreciationMockMvc.perform(post("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isBadRequest());

        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciations() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetDepreciation.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].depreciationAmount").value(hasItem(DEFAULT_DEPRECIATION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].depreciationDate").value(hasItem(DEFAULT_DEPRECIATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID.intValue())))
            .andExpect(jsonPath("$.[*].assetItemId").value(hasItem(DEFAULT_ASSET_ITEM_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getAssetDepreciation() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get the assetDepreciation
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations/{id}", assetDepreciation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(assetDepreciation.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.depreciationAmount").value(DEFAULT_DEPRECIATION_AMOUNT.intValue()))
            .andExpect(jsonPath("$.depreciationDate").value(DEFAULT_DEPRECIATION_DATE.toString()))
            .andExpect(jsonPath("$.categoryId").value(DEFAULT_CATEGORY_ID.intValue()))
            .andExpect(jsonPath("$.assetItemId").value(DEFAULT_ASSET_ITEM_ID.intValue()));
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description equals to DEFAULT_DESCRIPTION
        defaultAssetDepreciationShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the assetDepreciationList where description equals to UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description not equals to DEFAULT_DESCRIPTION
        defaultAssetDepreciationShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the assetDepreciationList where description not equals to UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the assetDepreciationList where description equals to UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description is not null
        defaultAssetDepreciationShouldBeFound("description.specified=true");

        // Get all the assetDepreciationList where description is null
        defaultAssetDepreciationShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description contains DEFAULT_DESCRIPTION
        defaultAssetDepreciationShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the assetDepreciationList where description contains UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where description does not contain DEFAULT_DESCRIPTION
        defaultAssetDepreciationShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the assetDepreciationList where description does not contain UPDATED_DESCRIPTION
        defaultAssetDepreciationShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount equals to DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.equals=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount equals to UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.equals=" + UPDATED_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount not equals to DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.notEquals=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount not equals to UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.notEquals=" + UPDATED_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsInShouldWork() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount in DEFAULT_DEPRECIATION_AMOUNT or UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.in=" + DEFAULT_DEPRECIATION_AMOUNT + "," + UPDATED_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount equals to UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.in=" + UPDATED_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount is not null
        defaultAssetDepreciationShouldBeFound("depreciationAmount.specified=true");

        // Get all the assetDepreciationList where depreciationAmount is null
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount is greater than or equal to DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.greaterThanOrEqual=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount is greater than or equal to UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.greaterThanOrEqual=" + UPDATED_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount is less than or equal to DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.lessThanOrEqual=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount is less than or equal to SMALLER_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.lessThanOrEqual=" + SMALLER_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount is less than DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.lessThan=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount is less than UPDATED_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.lessThan=" + UPDATED_DEPRECIATION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationAmount is greater than DEFAULT_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldNotBeFound("depreciationAmount.greaterThan=" + DEFAULT_DEPRECIATION_AMOUNT);

        // Get all the assetDepreciationList where depreciationAmount is greater than SMALLER_DEPRECIATION_AMOUNT
        defaultAssetDepreciationShouldBeFound("depreciationAmount.greaterThan=" + SMALLER_DEPRECIATION_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate equals to DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.equals=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate equals to UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.equals=" + UPDATED_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate not equals to DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.notEquals=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate not equals to UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.notEquals=" + UPDATED_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsInShouldWork() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate in DEFAULT_DEPRECIATION_DATE or UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.in=" + DEFAULT_DEPRECIATION_DATE + "," + UPDATED_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate equals to UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.in=" + UPDATED_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate is not null
        defaultAssetDepreciationShouldBeFound("depreciationDate.specified=true");

        // Get all the assetDepreciationList where depreciationDate is null
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate is greater than or equal to DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.greaterThanOrEqual=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate is greater than or equal to UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.greaterThanOrEqual=" + UPDATED_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate is less than or equal to DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.lessThanOrEqual=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate is less than or equal to SMALLER_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.lessThanOrEqual=" + SMALLER_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate is less than DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.lessThan=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate is less than UPDATED_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.lessThan=" + UPDATED_DEPRECIATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByDepreciationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where depreciationDate is greater than DEFAULT_DEPRECIATION_DATE
        defaultAssetDepreciationShouldNotBeFound("depreciationDate.greaterThan=" + DEFAULT_DEPRECIATION_DATE);

        // Get all the assetDepreciationList where depreciationDate is greater than SMALLER_DEPRECIATION_DATE
        defaultAssetDepreciationShouldBeFound("depreciationDate.greaterThan=" + SMALLER_DEPRECIATION_DATE);
    }


    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId equals to DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.equals=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId equals to UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.equals=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId not equals to DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.notEquals=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId not equals to UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.notEquals=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsInShouldWork() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId in DEFAULT_CATEGORY_ID or UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.in=" + DEFAULT_CATEGORY_ID + "," + UPDATED_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId equals to UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.in=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId is not null
        defaultAssetDepreciationShouldBeFound("categoryId.specified=true");

        // Get all the assetDepreciationList where categoryId is null
        defaultAssetDepreciationShouldNotBeFound("categoryId.specified=false");
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId is greater than or equal to DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.greaterThanOrEqual=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId is greater than or equal to UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.greaterThanOrEqual=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId is less than or equal to DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.lessThanOrEqual=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId is less than or equal to SMALLER_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.lessThanOrEqual=" + SMALLER_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsLessThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId is less than DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.lessThan=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId is less than UPDATED_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.lessThan=" + UPDATED_CATEGORY_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByCategoryIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where categoryId is greater than DEFAULT_CATEGORY_ID
        defaultAssetDepreciationShouldNotBeFound("categoryId.greaterThan=" + DEFAULT_CATEGORY_ID);

        // Get all the assetDepreciationList where categoryId is greater than SMALLER_CATEGORY_ID
        defaultAssetDepreciationShouldBeFound("categoryId.greaterThan=" + SMALLER_CATEGORY_ID);
    }


    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId equals to DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.equals=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId equals to UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.equals=" + UPDATED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId not equals to DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.notEquals=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId not equals to UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.notEquals=" + UPDATED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsInShouldWork() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId in DEFAULT_ASSET_ITEM_ID or UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.in=" + DEFAULT_ASSET_ITEM_ID + "," + UPDATED_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId equals to UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.in=" + UPDATED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId is not null
        defaultAssetDepreciationShouldBeFound("assetItemId.specified=true");

        // Get all the assetDepreciationList where assetItemId is null
        defaultAssetDepreciationShouldNotBeFound("assetItemId.specified=false");
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId is greater than or equal to DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.greaterThanOrEqual=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId is greater than or equal to UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.greaterThanOrEqual=" + UPDATED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId is less than or equal to DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.lessThanOrEqual=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId is less than or equal to SMALLER_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.lessThanOrEqual=" + SMALLER_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsLessThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId is less than DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.lessThan=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId is less than UPDATED_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.lessThan=" + UPDATED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllAssetDepreciationsByAssetItemIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        // Get all the assetDepreciationList where assetItemId is greater than DEFAULT_ASSET_ITEM_ID
        defaultAssetDepreciationShouldNotBeFound("assetItemId.greaterThan=" + DEFAULT_ASSET_ITEM_ID);

        // Get all the assetDepreciationList where assetItemId is greater than SMALLER_ASSET_ITEM_ID
        defaultAssetDepreciationShouldBeFound("assetItemId.greaterThan=" + SMALLER_ASSET_ITEM_ID);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssetDepreciationShouldBeFound(String filter) throws Exception {
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetDepreciation.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].depreciationAmount").value(hasItem(DEFAULT_DEPRECIATION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].depreciationDate").value(hasItem(DEFAULT_DEPRECIATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID.intValue())))
            .andExpect(jsonPath("$.[*].assetItemId").value(hasItem(DEFAULT_ASSET_ITEM_ID.intValue())));

        // Check, that the count call also returns 1
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssetDepreciationShouldNotBeFound(String filter) throws Exception {
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingAssetDepreciation() throws Exception {
        // Get the assetDepreciation
        restAssetDepreciationMockMvc.perform(get("/api/asset-depreciations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssetDepreciation() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        int databaseSizeBeforeUpdate = assetDepreciationRepository.findAll().size();

        // Update the assetDepreciation
        AssetDepreciation updatedAssetDepreciation = assetDepreciationRepository.findById(assetDepreciation.getId()).get();
        // Disconnect from session so that the updates on updatedAssetDepreciation are not directly saved in db
        em.detach(updatedAssetDepreciation);
        updatedAssetDepreciation
            .description(UPDATED_DESCRIPTION)
            .depreciationAmount(UPDATED_DEPRECIATION_AMOUNT)
            .depreciationDate(UPDATED_DEPRECIATION_DATE)
            .categoryId(UPDATED_CATEGORY_ID)
            .assetItemId(UPDATED_ASSET_ITEM_ID);
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(updatedAssetDepreciation);

        restAssetDepreciationMockMvc.perform(put("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isOk());

        // Validate the AssetDepreciation in the database
        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeUpdate);
        AssetDepreciation testAssetDepreciation = assetDepreciationList.get(assetDepreciationList.size() - 1);
        assertThat(testAssetDepreciation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAssetDepreciation.getDepreciationAmount()).isEqualTo(UPDATED_DEPRECIATION_AMOUNT);
        assertThat(testAssetDepreciation.getDepreciationDate()).isEqualTo(UPDATED_DEPRECIATION_DATE);
        assertThat(testAssetDepreciation.getCategoryId()).isEqualTo(UPDATED_CATEGORY_ID);
        assertThat(testAssetDepreciation.getAssetItemId()).isEqualTo(UPDATED_ASSET_ITEM_ID);

        // Validate the AssetDepreciation in Elasticsearch
        verify(mockAssetDepreciationSearchRepository, times(1)).save(testAssetDepreciation);
    }

    @Test
    @Transactional
    public void updateNonExistingAssetDepreciation() throws Exception {
        int databaseSizeBeforeUpdate = assetDepreciationRepository.findAll().size();

        // Create the AssetDepreciation
        AssetDepreciationDTO assetDepreciationDTO = assetDepreciationMapper.toDto(assetDepreciation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetDepreciationMockMvc.perform(put("/api/asset-depreciations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assetDepreciationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetDepreciation in the database
        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AssetDepreciation in Elasticsearch
        verify(mockAssetDepreciationSearchRepository, times(0)).save(assetDepreciation);
    }

    @Test
    @Transactional
    public void deleteAssetDepreciation() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);

        int databaseSizeBeforeDelete = assetDepreciationRepository.findAll().size();

        // Delete the assetDepreciation
        restAssetDepreciationMockMvc.perform(delete("/api/asset-depreciations/{id}", assetDepreciation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AssetDepreciation> assetDepreciationList = assetDepreciationRepository.findAll();
        assertThat(assetDepreciationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AssetDepreciation in Elasticsearch
        verify(mockAssetDepreciationSearchRepository, times(1)).deleteById(assetDepreciation.getId());
    }

    @Test
    @Transactional
    public void searchAssetDepreciation() throws Exception {
        // Initialize the database
        assetDepreciationRepository.saveAndFlush(assetDepreciation);
        when(mockAssetDepreciationSearchRepository.search(queryStringQuery("id:" + assetDepreciation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(assetDepreciation), PageRequest.of(0, 1), 1));
        // Search the assetDepreciation
        restAssetDepreciationMockMvc.perform(get("/api/_search/asset-depreciations?query=id:" + assetDepreciation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetDepreciation.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].depreciationAmount").value(hasItem(DEFAULT_DEPRECIATION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].depreciationDate").value(hasItem(DEFAULT_DEPRECIATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID.intValue())))
            .andExpect(jsonPath("$.[*].assetItemId").value(hasItem(DEFAULT_ASSET_ITEM_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetDepreciation.class);
        AssetDepreciation assetDepreciation1 = new AssetDepreciation();
        assetDepreciation1.setId(1L);
        AssetDepreciation assetDepreciation2 = new AssetDepreciation();
        assetDepreciation2.setId(assetDepreciation1.getId());
        assertThat(assetDepreciation1).isEqualTo(assetDepreciation2);
        assetDepreciation2.setId(2L);
        assertThat(assetDepreciation1).isNotEqualTo(assetDepreciation2);
        assetDepreciation1.setId(null);
        assertThat(assetDepreciation1).isNotEqualTo(assetDepreciation2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetDepreciationDTO.class);
        AssetDepreciationDTO assetDepreciationDTO1 = new AssetDepreciationDTO();
        assetDepreciationDTO1.setId(1L);
        AssetDepreciationDTO assetDepreciationDTO2 = new AssetDepreciationDTO();
        assertThat(assetDepreciationDTO1).isNotEqualTo(assetDepreciationDTO2);
        assetDepreciationDTO2.setId(assetDepreciationDTO1.getId());
        assertThat(assetDepreciationDTO1).isEqualTo(assetDepreciationDTO2);
        assetDepreciationDTO2.setId(2L);
        assertThat(assetDepreciationDTO1).isNotEqualTo(assetDepreciationDTO2);
        assetDepreciationDTO1.setId(null);
        assertThat(assetDepreciationDTO1).isNotEqualTo(assetDepreciationDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(assetDepreciationMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(assetDepreciationMapper.fromId(null)).isNull();
    }
}
