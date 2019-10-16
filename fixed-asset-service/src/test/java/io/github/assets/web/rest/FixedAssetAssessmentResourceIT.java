package io.github.assets.web.rest;

import io.github.assets.FixedAssetServiceApp;
import io.github.assets.config.SecurityBeanOverrideConfiguration;
import io.github.assets.domain.FixedAssetAssessment;
import io.github.assets.repository.FixedAssetAssessmentRepository;
import io.github.assets.repository.search.FixedAssetAssessmentSearchRepository;
import io.github.assets.service.FixedAssetAssessmentService;
import io.github.assets.service.dto.FixedAssetAssessmentDTO;
import io.github.assets.service.mapper.FixedAssetAssessmentMapper;
import io.github.assets.web.rest.errors.ExceptionTranslator;
import io.github.assets.service.dto.FixedAssetAssessmentCriteria;
import io.github.assets.service.FixedAssetAssessmentQueryService;

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
import org.springframework.util.Base64Utils;
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

import io.github.assets.domain.enumeration.AssetCondition;
/**
 * Integration tests for the {@link FixedAssetAssessmentResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FixedAssetServiceApp.class})
public class FixedAssetAssessmentResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final AssetCondition DEFAULT_ASSET_CONDITION = AssetCondition.EXCELLENT;
    private static final AssetCondition UPDATED_ASSET_CONDITION = AssetCondition.GOOD;

    private static final LocalDate DEFAULT_ASSESSMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSESSMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ASSESSMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_ASSESSMENT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_ASSESSMENT_REMARKS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_OF_ASSESSING_STAFF = "AAAAAAAAAA";
    private static final String UPDATED_NAME_OF_ASSESSING_STAFF = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR = "AAAAAAAAAA";
    private static final String UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_SERVICE_OUTLET_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_SERVICE_OUTLET_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_PHYSICAL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_PHYSICAL_ADDRESS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_NEXT_ASSESSMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_NEXT_ASSESSMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_NEXT_ASSESSMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NAME_OF_USER = "AAAAAAAAAA";
    private static final String UPDATED_NAME_OF_USER = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FIXED_ASSET_PICTURE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FIXED_ASSET_PICTURE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FIXED_ASSET_PICTURE_CONTENT_TYPE = "image/png";

    private static final Long DEFAULT_FIXED_ASSET_ITEM_ID = 1L;
    private static final Long UPDATED_FIXED_ASSET_ITEM_ID = 2L;
    private static final Long SMALLER_FIXED_ASSET_ITEM_ID = 1L - 1L;

    private static final BigDecimal DEFAULT_ESTIMATED_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ESTIMATED_VALUE = new BigDecimal(2);
    private static final BigDecimal SMALLER_ESTIMATED_VALUE = new BigDecimal(1 - 1);

    private static final Double DEFAULT_ESTIMATED_USEFUL_MONTHS = 1D;
    private static final Double UPDATED_ESTIMATED_USEFUL_MONTHS = 2D;
    private static final Double SMALLER_ESTIMATED_USEFUL_MONTHS = 1D - 1D;

    @Autowired
    private FixedAssetAssessmentRepository fixedAssetAssessmentRepository;

    @Autowired
    private FixedAssetAssessmentMapper fixedAssetAssessmentMapper;

    @Autowired
    private FixedAssetAssessmentService fixedAssetAssessmentService;

    /**
     * This repository is mocked in the io.github.assets.repository.search test package.
     *
     * @see io.github.assets.repository.search.FixedAssetAssessmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private FixedAssetAssessmentSearchRepository mockFixedAssetAssessmentSearchRepository;

    @Autowired
    private FixedAssetAssessmentQueryService fixedAssetAssessmentQueryService;

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

    private MockMvc restFixedAssetAssessmentMockMvc;

    private FixedAssetAssessment fixedAssetAssessment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FixedAssetAssessmentResource fixedAssetAssessmentResource = new FixedAssetAssessmentResource(fixedAssetAssessmentService, fixedAssetAssessmentQueryService);
        this.restFixedAssetAssessmentMockMvc = MockMvcBuilders.standaloneSetup(fixedAssetAssessmentResource)
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
    public static FixedAssetAssessment createEntity(EntityManager em) {
        FixedAssetAssessment fixedAssetAssessment = new FixedAssetAssessment()
            .description(DEFAULT_DESCRIPTION)
            .assetCondition(DEFAULT_ASSET_CONDITION)
            .assessmentDate(DEFAULT_ASSESSMENT_DATE)
            .assessmentRemarks(DEFAULT_ASSESSMENT_REMARKS)
            .nameOfAssessingStaff(DEFAULT_NAME_OF_ASSESSING_STAFF)
            .nameOfAssessmentContractor(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR)
            .currentServiceOutletCode(DEFAULT_CURRENT_SERVICE_OUTLET_CODE)
            .currentPhysicalAddress(DEFAULT_CURRENT_PHYSICAL_ADDRESS)
            .nextAssessmentDate(DEFAULT_NEXT_ASSESSMENT_DATE)
            .nameOfUser(DEFAULT_NAME_OF_USER)
            .fixedAssetPicture(DEFAULT_FIXED_ASSET_PICTURE)
            .fixedAssetPictureContentType(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE)
            .fixedAssetItemId(DEFAULT_FIXED_ASSET_ITEM_ID)
            .estimatedValue(DEFAULT_ESTIMATED_VALUE)
            .estimatedUsefulMonths(DEFAULT_ESTIMATED_USEFUL_MONTHS);
        return fixedAssetAssessment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FixedAssetAssessment createUpdatedEntity(EntityManager em) {
        FixedAssetAssessment fixedAssetAssessment = new FixedAssetAssessment()
            .description(UPDATED_DESCRIPTION)
            .assetCondition(UPDATED_ASSET_CONDITION)
            .assessmentDate(UPDATED_ASSESSMENT_DATE)
            .assessmentRemarks(UPDATED_ASSESSMENT_REMARKS)
            .nameOfAssessingStaff(UPDATED_NAME_OF_ASSESSING_STAFF)
            .nameOfAssessmentContractor(UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR)
            .currentServiceOutletCode(UPDATED_CURRENT_SERVICE_OUTLET_CODE)
            .currentPhysicalAddress(UPDATED_CURRENT_PHYSICAL_ADDRESS)
            .nextAssessmentDate(UPDATED_NEXT_ASSESSMENT_DATE)
            .nameOfUser(UPDATED_NAME_OF_USER)
            .fixedAssetPicture(UPDATED_FIXED_ASSET_PICTURE)
            .fixedAssetPictureContentType(UPDATED_FIXED_ASSET_PICTURE_CONTENT_TYPE)
            .fixedAssetItemId(UPDATED_FIXED_ASSET_ITEM_ID)
            .estimatedValue(UPDATED_ESTIMATED_VALUE)
            .estimatedUsefulMonths(UPDATED_ESTIMATED_USEFUL_MONTHS);
        return fixedAssetAssessment;
    }

    @BeforeEach
    public void initTest() {
        fixedAssetAssessment = createEntity(em);
    }

    @Test
    @Transactional
    public void createFixedAssetAssessment() throws Exception {
        int databaseSizeBeforeCreate = fixedAssetAssessmentRepository.findAll().size();

        // Create the FixedAssetAssessment
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO = fixedAssetAssessmentMapper.toDto(fixedAssetAssessment);
        restFixedAssetAssessmentMockMvc.perform(post("/api/fixed-asset-assessments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fixedAssetAssessmentDTO)))
            .andExpect(status().isCreated());

        // Validate the FixedAssetAssessment in the database
        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeCreate + 1);
        FixedAssetAssessment testFixedAssetAssessment = fixedAssetAssessmentList.get(fixedAssetAssessmentList.size() - 1);
        assertThat(testFixedAssetAssessment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFixedAssetAssessment.getAssetCondition()).isEqualTo(DEFAULT_ASSET_CONDITION);
        assertThat(testFixedAssetAssessment.getAssessmentDate()).isEqualTo(DEFAULT_ASSESSMENT_DATE);
        assertThat(testFixedAssetAssessment.getAssessmentRemarks()).isEqualTo(DEFAULT_ASSESSMENT_REMARKS);
        assertThat(testFixedAssetAssessment.getNameOfAssessingStaff()).isEqualTo(DEFAULT_NAME_OF_ASSESSING_STAFF);
        assertThat(testFixedAssetAssessment.getNameOfAssessmentContractor()).isEqualTo(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR);
        assertThat(testFixedAssetAssessment.getCurrentServiceOutletCode()).isEqualTo(DEFAULT_CURRENT_SERVICE_OUTLET_CODE);
        assertThat(testFixedAssetAssessment.getCurrentPhysicalAddress()).isEqualTo(DEFAULT_CURRENT_PHYSICAL_ADDRESS);
        assertThat(testFixedAssetAssessment.getNextAssessmentDate()).isEqualTo(DEFAULT_NEXT_ASSESSMENT_DATE);
        assertThat(testFixedAssetAssessment.getNameOfUser()).isEqualTo(DEFAULT_NAME_OF_USER);
        assertThat(testFixedAssetAssessment.getFixedAssetPicture()).isEqualTo(DEFAULT_FIXED_ASSET_PICTURE);
        assertThat(testFixedAssetAssessment.getFixedAssetPictureContentType()).isEqualTo(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE);
        assertThat(testFixedAssetAssessment.getFixedAssetItemId()).isEqualTo(DEFAULT_FIXED_ASSET_ITEM_ID);
        assertThat(testFixedAssetAssessment.getEstimatedValue()).isEqualTo(DEFAULT_ESTIMATED_VALUE);
        assertThat(testFixedAssetAssessment.getEstimatedUsefulMonths()).isEqualTo(DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Validate the FixedAssetAssessment in Elasticsearch
        verify(mockFixedAssetAssessmentSearchRepository, times(1)).save(testFixedAssetAssessment);
    }

    @Test
    @Transactional
    public void createFixedAssetAssessmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fixedAssetAssessmentRepository.findAll().size();

        // Create the FixedAssetAssessment with an existing ID
        fixedAssetAssessment.setId(1L);
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO = fixedAssetAssessmentMapper.toDto(fixedAssetAssessment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFixedAssetAssessmentMockMvc.perform(post("/api/fixed-asset-assessments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fixedAssetAssessmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FixedAssetAssessment in the database
        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the FixedAssetAssessment in Elasticsearch
        verify(mockFixedAssetAssessmentSearchRepository, times(0)).save(fixedAssetAssessment);
    }


    @Test
    @Transactional
    public void checkAssetConditionIsRequired() throws Exception {
        int databaseSizeBeforeTest = fixedAssetAssessmentRepository.findAll().size();
        // set the field null
        fixedAssetAssessment.setAssetCondition(null);

        // Create the FixedAssetAssessment, which fails.
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO = fixedAssetAssessmentMapper.toDto(fixedAssetAssessment);

        restFixedAssetAssessmentMockMvc.perform(post("/api/fixed-asset-assessments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fixedAssetAssessmentDTO)))
            .andExpect(status().isBadRequest());

        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessments() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fixedAssetAssessment.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].assetCondition").value(hasItem(DEFAULT_ASSET_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].assessmentDate").value(hasItem(DEFAULT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assessmentRemarks").value(hasItem(DEFAULT_ASSESSMENT_REMARKS)))
            .andExpect(jsonPath("$.[*].nameOfAssessingStaff").value(hasItem(DEFAULT_NAME_OF_ASSESSING_STAFF)))
            .andExpect(jsonPath("$.[*].nameOfAssessmentContractor").value(hasItem(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR)))
            .andExpect(jsonPath("$.[*].currentServiceOutletCode").value(hasItem(DEFAULT_CURRENT_SERVICE_OUTLET_CODE)))
            .andExpect(jsonPath("$.[*].currentPhysicalAddress").value(hasItem(DEFAULT_CURRENT_PHYSICAL_ADDRESS)))
            .andExpect(jsonPath("$.[*].nextAssessmentDate").value(hasItem(DEFAULT_NEXT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nameOfUser").value(hasItem(DEFAULT_NAME_OF_USER)))
            .andExpect(jsonPath("$.[*].fixedAssetPictureContentType").value(hasItem(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fixedAssetPicture").value(hasItem(Base64Utils.encodeToString(DEFAULT_FIXED_ASSET_PICTURE))))
            .andExpect(jsonPath("$.[*].fixedAssetItemId").value(hasItem(DEFAULT_FIXED_ASSET_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].estimatedValue").value(hasItem(DEFAULT_ESTIMATED_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].estimatedUsefulMonths").value(hasItem(DEFAULT_ESTIMATED_USEFUL_MONTHS.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getFixedAssetAssessment() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get the fixedAssetAssessment
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments/{id}", fixedAssetAssessment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fixedAssetAssessment.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.assetCondition").value(DEFAULT_ASSET_CONDITION.toString()))
            .andExpect(jsonPath("$.assessmentDate").value(DEFAULT_ASSESSMENT_DATE.toString()))
            .andExpect(jsonPath("$.assessmentRemarks").value(DEFAULT_ASSESSMENT_REMARKS))
            .andExpect(jsonPath("$.nameOfAssessingStaff").value(DEFAULT_NAME_OF_ASSESSING_STAFF))
            .andExpect(jsonPath("$.nameOfAssessmentContractor").value(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR))
            .andExpect(jsonPath("$.currentServiceOutletCode").value(DEFAULT_CURRENT_SERVICE_OUTLET_CODE))
            .andExpect(jsonPath("$.currentPhysicalAddress").value(DEFAULT_CURRENT_PHYSICAL_ADDRESS))
            .andExpect(jsonPath("$.nextAssessmentDate").value(DEFAULT_NEXT_ASSESSMENT_DATE.toString()))
            .andExpect(jsonPath("$.nameOfUser").value(DEFAULT_NAME_OF_USER))
            .andExpect(jsonPath("$.fixedAssetPictureContentType").value(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE))
            .andExpect(jsonPath("$.fixedAssetPicture").value(Base64Utils.encodeToString(DEFAULT_FIXED_ASSET_PICTURE)))
            .andExpect(jsonPath("$.fixedAssetItemId").value(DEFAULT_FIXED_ASSET_ITEM_ID.intValue()))
            .andExpect(jsonPath("$.estimatedValue").value(DEFAULT_ESTIMATED_VALUE.intValue()))
            .andExpect(jsonPath("$.estimatedUsefulMonths").value(DEFAULT_ESTIMATED_USEFUL_MONTHS.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description equals to DEFAULT_DESCRIPTION
        defaultFixedAssetAssessmentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the fixedAssetAssessmentList where description equals to UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description not equals to DEFAULT_DESCRIPTION
        defaultFixedAssetAssessmentShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the fixedAssetAssessmentList where description not equals to UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the fixedAssetAssessmentList where description equals to UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description is not null
        defaultFixedAssetAssessmentShouldBeFound("description.specified=true");

        // Get all the fixedAssetAssessmentList where description is null
        defaultFixedAssetAssessmentShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description contains DEFAULT_DESCRIPTION
        defaultFixedAssetAssessmentShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the fixedAssetAssessmentList where description contains UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where description does not contain DEFAULT_DESCRIPTION
        defaultFixedAssetAssessmentShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the fixedAssetAssessmentList where description does not contain UPDATED_DESCRIPTION
        defaultFixedAssetAssessmentShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssetConditionIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assetCondition equals to DEFAULT_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldBeFound("assetCondition.equals=" + DEFAULT_ASSET_CONDITION);

        // Get all the fixedAssetAssessmentList where assetCondition equals to UPDATED_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldNotBeFound("assetCondition.equals=" + UPDATED_ASSET_CONDITION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssetConditionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assetCondition not equals to DEFAULT_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldNotBeFound("assetCondition.notEquals=" + DEFAULT_ASSET_CONDITION);

        // Get all the fixedAssetAssessmentList where assetCondition not equals to UPDATED_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldBeFound("assetCondition.notEquals=" + UPDATED_ASSET_CONDITION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssetConditionIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assetCondition in DEFAULT_ASSET_CONDITION or UPDATED_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldBeFound("assetCondition.in=" + DEFAULT_ASSET_CONDITION + "," + UPDATED_ASSET_CONDITION);

        // Get all the fixedAssetAssessmentList where assetCondition equals to UPDATED_ASSET_CONDITION
        defaultFixedAssetAssessmentShouldNotBeFound("assetCondition.in=" + UPDATED_ASSET_CONDITION);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssetConditionIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assetCondition is not null
        defaultFixedAssetAssessmentShouldBeFound("assetCondition.specified=true");

        // Get all the fixedAssetAssessmentList where assetCondition is null
        defaultFixedAssetAssessmentShouldNotBeFound("assetCondition.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate equals to DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.equals=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate equals to UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.equals=" + UPDATED_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate not equals to DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.notEquals=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate not equals to UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.notEquals=" + UPDATED_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate in DEFAULT_ASSESSMENT_DATE or UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.in=" + DEFAULT_ASSESSMENT_DATE + "," + UPDATED_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate equals to UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.in=" + UPDATED_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate is not null
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.specified=true");

        // Get all the fixedAssetAssessmentList where assessmentDate is null
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate is greater than or equal to DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.greaterThanOrEqual=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate is greater than or equal to UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.greaterThanOrEqual=" + UPDATED_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate is less than or equal to DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.lessThanOrEqual=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate is less than or equal to SMALLER_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.lessThanOrEqual=" + SMALLER_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate is less than DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.lessThan=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate is less than UPDATED_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.lessThan=" + UPDATED_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentDate is greater than DEFAULT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentDate.greaterThan=" + DEFAULT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where assessmentDate is greater than SMALLER_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("assessmentDate.greaterThan=" + SMALLER_ASSESSMENT_DATE);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks equals to DEFAULT_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.equals=" + DEFAULT_ASSESSMENT_REMARKS);

        // Get all the fixedAssetAssessmentList where assessmentRemarks equals to UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.equals=" + UPDATED_ASSESSMENT_REMARKS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks not equals to DEFAULT_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.notEquals=" + DEFAULT_ASSESSMENT_REMARKS);

        // Get all the fixedAssetAssessmentList where assessmentRemarks not equals to UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.notEquals=" + UPDATED_ASSESSMENT_REMARKS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks in DEFAULT_ASSESSMENT_REMARKS or UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.in=" + DEFAULT_ASSESSMENT_REMARKS + "," + UPDATED_ASSESSMENT_REMARKS);

        // Get all the fixedAssetAssessmentList where assessmentRemarks equals to UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.in=" + UPDATED_ASSESSMENT_REMARKS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks is not null
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.specified=true");

        // Get all the fixedAssetAssessmentList where assessmentRemarks is null
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks contains DEFAULT_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.contains=" + DEFAULT_ASSESSMENT_REMARKS);

        // Get all the fixedAssetAssessmentList where assessmentRemarks contains UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.contains=" + UPDATED_ASSESSMENT_REMARKS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByAssessmentRemarksNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where assessmentRemarks does not contain DEFAULT_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldNotBeFound("assessmentRemarks.doesNotContain=" + DEFAULT_ASSESSMENT_REMARKS);

        // Get all the fixedAssetAssessmentList where assessmentRemarks does not contain UPDATED_ASSESSMENT_REMARKS
        defaultFixedAssetAssessmentShouldBeFound("assessmentRemarks.doesNotContain=" + UPDATED_ASSESSMENT_REMARKS);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff equals to DEFAULT_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.equals=" + DEFAULT_NAME_OF_ASSESSING_STAFF);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff equals to UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.equals=" + UPDATED_NAME_OF_ASSESSING_STAFF);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff not equals to DEFAULT_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.notEquals=" + DEFAULT_NAME_OF_ASSESSING_STAFF);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff not equals to UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.notEquals=" + UPDATED_NAME_OF_ASSESSING_STAFF);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff in DEFAULT_NAME_OF_ASSESSING_STAFF or UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.in=" + DEFAULT_NAME_OF_ASSESSING_STAFF + "," + UPDATED_NAME_OF_ASSESSING_STAFF);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff equals to UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.in=" + UPDATED_NAME_OF_ASSESSING_STAFF);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff is not null
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.specified=true");

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff is null
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff contains DEFAULT_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.contains=" + DEFAULT_NAME_OF_ASSESSING_STAFF);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff contains UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.contains=" + UPDATED_NAME_OF_ASSESSING_STAFF);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessingStaffNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff does not contain DEFAULT_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessingStaff.doesNotContain=" + DEFAULT_NAME_OF_ASSESSING_STAFF);

        // Get all the fixedAssetAssessmentList where nameOfAssessingStaff does not contain UPDATED_NAME_OF_ASSESSING_STAFF
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessingStaff.doesNotContain=" + UPDATED_NAME_OF_ASSESSING_STAFF);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor equals to DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.equals=" + DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor equals to UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.equals=" + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor not equals to DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.notEquals=" + DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor not equals to UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.notEquals=" + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor in DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR or UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.in=" + DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR + "," + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor equals to UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.in=" + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor is not null
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.specified=true");

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor is null
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor contains DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.contains=" + DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor contains UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.contains=" + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfAssessmentContractorNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor does not contain DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfAssessmentContractor.doesNotContain=" + DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR);

        // Get all the fixedAssetAssessmentList where nameOfAssessmentContractor does not contain UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR
        defaultFixedAssetAssessmentShouldBeFound("nameOfAssessmentContractor.doesNotContain=" + UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode equals to DEFAULT_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.equals=" + DEFAULT_CURRENT_SERVICE_OUTLET_CODE);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode equals to UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.equals=" + UPDATED_CURRENT_SERVICE_OUTLET_CODE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode not equals to DEFAULT_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.notEquals=" + DEFAULT_CURRENT_SERVICE_OUTLET_CODE);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode not equals to UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.notEquals=" + UPDATED_CURRENT_SERVICE_OUTLET_CODE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode in DEFAULT_CURRENT_SERVICE_OUTLET_CODE or UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.in=" + DEFAULT_CURRENT_SERVICE_OUTLET_CODE + "," + UPDATED_CURRENT_SERVICE_OUTLET_CODE);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode equals to UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.in=" + UPDATED_CURRENT_SERVICE_OUTLET_CODE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode is not null
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.specified=true");

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode is null
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode contains DEFAULT_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.contains=" + DEFAULT_CURRENT_SERVICE_OUTLET_CODE);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode contains UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.contains=" + UPDATED_CURRENT_SERVICE_OUTLET_CODE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentServiceOutletCodeNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode does not contain DEFAULT_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldNotBeFound("currentServiceOutletCode.doesNotContain=" + DEFAULT_CURRENT_SERVICE_OUTLET_CODE);

        // Get all the fixedAssetAssessmentList where currentServiceOutletCode does not contain UPDATED_CURRENT_SERVICE_OUTLET_CODE
        defaultFixedAssetAssessmentShouldBeFound("currentServiceOutletCode.doesNotContain=" + UPDATED_CURRENT_SERVICE_OUTLET_CODE);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress equals to DEFAULT_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.equals=" + DEFAULT_CURRENT_PHYSICAL_ADDRESS);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress equals to UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.equals=" + UPDATED_CURRENT_PHYSICAL_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress not equals to DEFAULT_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.notEquals=" + DEFAULT_CURRENT_PHYSICAL_ADDRESS);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress not equals to UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.notEquals=" + UPDATED_CURRENT_PHYSICAL_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress in DEFAULT_CURRENT_PHYSICAL_ADDRESS or UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.in=" + DEFAULT_CURRENT_PHYSICAL_ADDRESS + "," + UPDATED_CURRENT_PHYSICAL_ADDRESS);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress equals to UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.in=" + UPDATED_CURRENT_PHYSICAL_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress is not null
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.specified=true");

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress is null
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress contains DEFAULT_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.contains=" + DEFAULT_CURRENT_PHYSICAL_ADDRESS);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress contains UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.contains=" + UPDATED_CURRENT_PHYSICAL_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByCurrentPhysicalAddressNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress does not contain DEFAULT_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldNotBeFound("currentPhysicalAddress.doesNotContain=" + DEFAULT_CURRENT_PHYSICAL_ADDRESS);

        // Get all the fixedAssetAssessmentList where currentPhysicalAddress does not contain UPDATED_CURRENT_PHYSICAL_ADDRESS
        defaultFixedAssetAssessmentShouldBeFound("currentPhysicalAddress.doesNotContain=" + UPDATED_CURRENT_PHYSICAL_ADDRESS);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate equals to DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.equals=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate equals to UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.equals=" + UPDATED_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate not equals to DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.notEquals=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate not equals to UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.notEquals=" + UPDATED_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate in DEFAULT_NEXT_ASSESSMENT_DATE or UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.in=" + DEFAULT_NEXT_ASSESSMENT_DATE + "," + UPDATED_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate equals to UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.in=" + UPDATED_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is not null
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.specified=true");

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is null
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is greater than or equal to DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.greaterThanOrEqual=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is greater than or equal to UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.greaterThanOrEqual=" + UPDATED_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is less than or equal to DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.lessThanOrEqual=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is less than or equal to SMALLER_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.lessThanOrEqual=" + SMALLER_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is less than DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.lessThan=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is less than UPDATED_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.lessThan=" + UPDATED_NEXT_ASSESSMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNextAssessmentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is greater than DEFAULT_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldNotBeFound("nextAssessmentDate.greaterThan=" + DEFAULT_NEXT_ASSESSMENT_DATE);

        // Get all the fixedAssetAssessmentList where nextAssessmentDate is greater than SMALLER_NEXT_ASSESSMENT_DATE
        defaultFixedAssetAssessmentShouldBeFound("nextAssessmentDate.greaterThan=" + SMALLER_NEXT_ASSESSMENT_DATE);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser equals to DEFAULT_NAME_OF_USER
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.equals=" + DEFAULT_NAME_OF_USER);

        // Get all the fixedAssetAssessmentList where nameOfUser equals to UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.equals=" + UPDATED_NAME_OF_USER);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser not equals to DEFAULT_NAME_OF_USER
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.notEquals=" + DEFAULT_NAME_OF_USER);

        // Get all the fixedAssetAssessmentList where nameOfUser not equals to UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.notEquals=" + UPDATED_NAME_OF_USER);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser in DEFAULT_NAME_OF_USER or UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.in=" + DEFAULT_NAME_OF_USER + "," + UPDATED_NAME_OF_USER);

        // Get all the fixedAssetAssessmentList where nameOfUser equals to UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.in=" + UPDATED_NAME_OF_USER);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser is not null
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.specified=true");

        // Get all the fixedAssetAssessmentList where nameOfUser is null
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.specified=false");
    }
                @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser contains DEFAULT_NAME_OF_USER
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.contains=" + DEFAULT_NAME_OF_USER);

        // Get all the fixedAssetAssessmentList where nameOfUser contains UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.contains=" + UPDATED_NAME_OF_USER);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByNameOfUserNotContainsSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where nameOfUser does not contain DEFAULT_NAME_OF_USER
        defaultFixedAssetAssessmentShouldNotBeFound("nameOfUser.doesNotContain=" + DEFAULT_NAME_OF_USER);

        // Get all the fixedAssetAssessmentList where nameOfUser does not contain UPDATED_NAME_OF_USER
        defaultFixedAssetAssessmentShouldBeFound("nameOfUser.doesNotContain=" + UPDATED_NAME_OF_USER);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId equals to DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.equals=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId equals to UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.equals=" + UPDATED_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId not equals to DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.notEquals=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId not equals to UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.notEquals=" + UPDATED_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId in DEFAULT_FIXED_ASSET_ITEM_ID or UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.in=" + DEFAULT_FIXED_ASSET_ITEM_ID + "," + UPDATED_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId equals to UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.in=" + UPDATED_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is not null
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.specified=true");

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is null
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is greater than or equal to DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.greaterThanOrEqual=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is greater than or equal to UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.greaterThanOrEqual=" + UPDATED_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is less than or equal to DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.lessThanOrEqual=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is less than or equal to SMALLER_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.lessThanOrEqual=" + SMALLER_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsLessThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is less than DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.lessThan=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is less than UPDATED_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.lessThan=" + UPDATED_FIXED_ASSET_ITEM_ID);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByFixedAssetItemIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is greater than DEFAULT_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldNotBeFound("fixedAssetItemId.greaterThan=" + DEFAULT_FIXED_ASSET_ITEM_ID);

        // Get all the fixedAssetAssessmentList where fixedAssetItemId is greater than SMALLER_FIXED_ASSET_ITEM_ID
        defaultFixedAssetAssessmentShouldBeFound("fixedAssetItemId.greaterThan=" + SMALLER_FIXED_ASSET_ITEM_ID);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue equals to DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.equals=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue equals to UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.equals=" + UPDATED_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue not equals to DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.notEquals=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue not equals to UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.notEquals=" + UPDATED_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue in DEFAULT_ESTIMATED_VALUE or UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.in=" + DEFAULT_ESTIMATED_VALUE + "," + UPDATED_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue equals to UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.in=" + UPDATED_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue is not null
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.specified=true");

        // Get all the fixedAssetAssessmentList where estimatedValue is null
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue is greater than or equal to DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.greaterThanOrEqual=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue is greater than or equal to UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.greaterThanOrEqual=" + UPDATED_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue is less than or equal to DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.lessThanOrEqual=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue is less than or equal to SMALLER_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.lessThanOrEqual=" + SMALLER_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsLessThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue is less than DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.lessThan=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue is less than UPDATED_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.lessThan=" + UPDATED_ESTIMATED_VALUE);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedValue is greater than DEFAULT_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedValue.greaterThan=" + DEFAULT_ESTIMATED_VALUE);

        // Get all the fixedAssetAssessmentList where estimatedValue is greater than SMALLER_ESTIMATED_VALUE
        defaultFixedAssetAssessmentShouldBeFound("estimatedValue.greaterThan=" + SMALLER_ESTIMATED_VALUE);
    }


    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths equals to DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.equals=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths equals to UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.equals=" + UPDATED_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths not equals to DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.notEquals=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths not equals to UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.notEquals=" + UPDATED_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsInShouldWork() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths in DEFAULT_ESTIMATED_USEFUL_MONTHS or UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.in=" + DEFAULT_ESTIMATED_USEFUL_MONTHS + "," + UPDATED_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths equals to UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.in=" + UPDATED_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsNullOrNotNull() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is not null
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.specified=true");

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is null
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.specified=false");
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is greater than or equal to DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.greaterThanOrEqual=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is greater than or equal to UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.greaterThanOrEqual=" + UPDATED_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is less than or equal to DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.lessThanOrEqual=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is less than or equal to SMALLER_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.lessThanOrEqual=" + SMALLER_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsLessThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is less than DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.lessThan=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is less than UPDATED_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.lessThan=" + UPDATED_ESTIMATED_USEFUL_MONTHS);
    }

    @Test
    @Transactional
    public void getAllFixedAssetAssessmentsByEstimatedUsefulMonthsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is greater than DEFAULT_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldNotBeFound("estimatedUsefulMonths.greaterThan=" + DEFAULT_ESTIMATED_USEFUL_MONTHS);

        // Get all the fixedAssetAssessmentList where estimatedUsefulMonths is greater than SMALLER_ESTIMATED_USEFUL_MONTHS
        defaultFixedAssetAssessmentShouldBeFound("estimatedUsefulMonths.greaterThan=" + SMALLER_ESTIMATED_USEFUL_MONTHS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFixedAssetAssessmentShouldBeFound(String filter) throws Exception {
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fixedAssetAssessment.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].assetCondition").value(hasItem(DEFAULT_ASSET_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].assessmentDate").value(hasItem(DEFAULT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assessmentRemarks").value(hasItem(DEFAULT_ASSESSMENT_REMARKS)))
            .andExpect(jsonPath("$.[*].nameOfAssessingStaff").value(hasItem(DEFAULT_NAME_OF_ASSESSING_STAFF)))
            .andExpect(jsonPath("$.[*].nameOfAssessmentContractor").value(hasItem(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR)))
            .andExpect(jsonPath("$.[*].currentServiceOutletCode").value(hasItem(DEFAULT_CURRENT_SERVICE_OUTLET_CODE)))
            .andExpect(jsonPath("$.[*].currentPhysicalAddress").value(hasItem(DEFAULT_CURRENT_PHYSICAL_ADDRESS)))
            .andExpect(jsonPath("$.[*].nextAssessmentDate").value(hasItem(DEFAULT_NEXT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nameOfUser").value(hasItem(DEFAULT_NAME_OF_USER)))
            .andExpect(jsonPath("$.[*].fixedAssetPictureContentType").value(hasItem(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fixedAssetPicture").value(hasItem(Base64Utils.encodeToString(DEFAULT_FIXED_ASSET_PICTURE))))
            .andExpect(jsonPath("$.[*].fixedAssetItemId").value(hasItem(DEFAULT_FIXED_ASSET_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].estimatedValue").value(hasItem(DEFAULT_ESTIMATED_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].estimatedUsefulMonths").value(hasItem(DEFAULT_ESTIMATED_USEFUL_MONTHS.doubleValue())));

        // Check, that the count call also returns 1
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFixedAssetAssessmentShouldNotBeFound(String filter) throws Exception {
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingFixedAssetAssessment() throws Exception {
        // Get the fixedAssetAssessment
        restFixedAssetAssessmentMockMvc.perform(get("/api/fixed-asset-assessments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFixedAssetAssessment() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        int databaseSizeBeforeUpdate = fixedAssetAssessmentRepository.findAll().size();

        // Update the fixedAssetAssessment
        FixedAssetAssessment updatedFixedAssetAssessment = fixedAssetAssessmentRepository.findById(fixedAssetAssessment.getId()).get();
        // Disconnect from session so that the updates on updatedFixedAssetAssessment are not directly saved in db
        em.detach(updatedFixedAssetAssessment);
        updatedFixedAssetAssessment
            .description(UPDATED_DESCRIPTION)
            .assetCondition(UPDATED_ASSET_CONDITION)
            .assessmentDate(UPDATED_ASSESSMENT_DATE)
            .assessmentRemarks(UPDATED_ASSESSMENT_REMARKS)
            .nameOfAssessingStaff(UPDATED_NAME_OF_ASSESSING_STAFF)
            .nameOfAssessmentContractor(UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR)
            .currentServiceOutletCode(UPDATED_CURRENT_SERVICE_OUTLET_CODE)
            .currentPhysicalAddress(UPDATED_CURRENT_PHYSICAL_ADDRESS)
            .nextAssessmentDate(UPDATED_NEXT_ASSESSMENT_DATE)
            .nameOfUser(UPDATED_NAME_OF_USER)
            .fixedAssetPicture(UPDATED_FIXED_ASSET_PICTURE)
            .fixedAssetPictureContentType(UPDATED_FIXED_ASSET_PICTURE_CONTENT_TYPE)
            .fixedAssetItemId(UPDATED_FIXED_ASSET_ITEM_ID)
            .estimatedValue(UPDATED_ESTIMATED_VALUE)
            .estimatedUsefulMonths(UPDATED_ESTIMATED_USEFUL_MONTHS);
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO = fixedAssetAssessmentMapper.toDto(updatedFixedAssetAssessment);

        restFixedAssetAssessmentMockMvc.perform(put("/api/fixed-asset-assessments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fixedAssetAssessmentDTO)))
            .andExpect(status().isOk());

        // Validate the FixedAssetAssessment in the database
        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeUpdate);
        FixedAssetAssessment testFixedAssetAssessment = fixedAssetAssessmentList.get(fixedAssetAssessmentList.size() - 1);
        assertThat(testFixedAssetAssessment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFixedAssetAssessment.getAssetCondition()).isEqualTo(UPDATED_ASSET_CONDITION);
        assertThat(testFixedAssetAssessment.getAssessmentDate()).isEqualTo(UPDATED_ASSESSMENT_DATE);
        assertThat(testFixedAssetAssessment.getAssessmentRemarks()).isEqualTo(UPDATED_ASSESSMENT_REMARKS);
        assertThat(testFixedAssetAssessment.getNameOfAssessingStaff()).isEqualTo(UPDATED_NAME_OF_ASSESSING_STAFF);
        assertThat(testFixedAssetAssessment.getNameOfAssessmentContractor()).isEqualTo(UPDATED_NAME_OF_ASSESSMENT_CONTRACTOR);
        assertThat(testFixedAssetAssessment.getCurrentServiceOutletCode()).isEqualTo(UPDATED_CURRENT_SERVICE_OUTLET_CODE);
        assertThat(testFixedAssetAssessment.getCurrentPhysicalAddress()).isEqualTo(UPDATED_CURRENT_PHYSICAL_ADDRESS);
        assertThat(testFixedAssetAssessment.getNextAssessmentDate()).isEqualTo(UPDATED_NEXT_ASSESSMENT_DATE);
        assertThat(testFixedAssetAssessment.getNameOfUser()).isEqualTo(UPDATED_NAME_OF_USER);
        assertThat(testFixedAssetAssessment.getFixedAssetPicture()).isEqualTo(UPDATED_FIXED_ASSET_PICTURE);
        assertThat(testFixedAssetAssessment.getFixedAssetPictureContentType()).isEqualTo(UPDATED_FIXED_ASSET_PICTURE_CONTENT_TYPE);
        assertThat(testFixedAssetAssessment.getFixedAssetItemId()).isEqualTo(UPDATED_FIXED_ASSET_ITEM_ID);
        assertThat(testFixedAssetAssessment.getEstimatedValue()).isEqualTo(UPDATED_ESTIMATED_VALUE);
        assertThat(testFixedAssetAssessment.getEstimatedUsefulMonths()).isEqualTo(UPDATED_ESTIMATED_USEFUL_MONTHS);

        // Validate the FixedAssetAssessment in Elasticsearch
        verify(mockFixedAssetAssessmentSearchRepository, times(1)).save(testFixedAssetAssessment);
    }

    @Test
    @Transactional
    public void updateNonExistingFixedAssetAssessment() throws Exception {
        int databaseSizeBeforeUpdate = fixedAssetAssessmentRepository.findAll().size();

        // Create the FixedAssetAssessment
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO = fixedAssetAssessmentMapper.toDto(fixedAssetAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFixedAssetAssessmentMockMvc.perform(put("/api/fixed-asset-assessments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fixedAssetAssessmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FixedAssetAssessment in the database
        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FixedAssetAssessment in Elasticsearch
        verify(mockFixedAssetAssessmentSearchRepository, times(0)).save(fixedAssetAssessment);
    }

    @Test
    @Transactional
    public void deleteFixedAssetAssessment() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);

        int databaseSizeBeforeDelete = fixedAssetAssessmentRepository.findAll().size();

        // Delete the fixedAssetAssessment
        restFixedAssetAssessmentMockMvc.perform(delete("/api/fixed-asset-assessments/{id}", fixedAssetAssessment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FixedAssetAssessment> fixedAssetAssessmentList = fixedAssetAssessmentRepository.findAll();
        assertThat(fixedAssetAssessmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FixedAssetAssessment in Elasticsearch
        verify(mockFixedAssetAssessmentSearchRepository, times(1)).deleteById(fixedAssetAssessment.getId());
    }

    @Test
    @Transactional
    public void searchFixedAssetAssessment() throws Exception {
        // Initialize the database
        fixedAssetAssessmentRepository.saveAndFlush(fixedAssetAssessment);
        when(mockFixedAssetAssessmentSearchRepository.search(queryStringQuery("id:" + fixedAssetAssessment.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(fixedAssetAssessment), PageRequest.of(0, 1), 1));
        // Search the fixedAssetAssessment
        restFixedAssetAssessmentMockMvc.perform(get("/api/_search/fixed-asset-assessments?query=id:" + fixedAssetAssessment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fixedAssetAssessment.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].assetCondition").value(hasItem(DEFAULT_ASSET_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].assessmentDate").value(hasItem(DEFAULT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assessmentRemarks").value(hasItem(DEFAULT_ASSESSMENT_REMARKS)))
            .andExpect(jsonPath("$.[*].nameOfAssessingStaff").value(hasItem(DEFAULT_NAME_OF_ASSESSING_STAFF)))
            .andExpect(jsonPath("$.[*].nameOfAssessmentContractor").value(hasItem(DEFAULT_NAME_OF_ASSESSMENT_CONTRACTOR)))
            .andExpect(jsonPath("$.[*].currentServiceOutletCode").value(hasItem(DEFAULT_CURRENT_SERVICE_OUTLET_CODE)))
            .andExpect(jsonPath("$.[*].currentPhysicalAddress").value(hasItem(DEFAULT_CURRENT_PHYSICAL_ADDRESS)))
            .andExpect(jsonPath("$.[*].nextAssessmentDate").value(hasItem(DEFAULT_NEXT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nameOfUser").value(hasItem(DEFAULT_NAME_OF_USER)))
            .andExpect(jsonPath("$.[*].fixedAssetPictureContentType").value(hasItem(DEFAULT_FIXED_ASSET_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fixedAssetPicture").value(hasItem(Base64Utils.encodeToString(DEFAULT_FIXED_ASSET_PICTURE))))
            .andExpect(jsonPath("$.[*].fixedAssetItemId").value(hasItem(DEFAULT_FIXED_ASSET_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].estimatedValue").value(hasItem(DEFAULT_ESTIMATED_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].estimatedUsefulMonths").value(hasItem(DEFAULT_ESTIMATED_USEFUL_MONTHS.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FixedAssetAssessment.class);
        FixedAssetAssessment fixedAssetAssessment1 = new FixedAssetAssessment();
        fixedAssetAssessment1.setId(1L);
        FixedAssetAssessment fixedAssetAssessment2 = new FixedAssetAssessment();
        fixedAssetAssessment2.setId(fixedAssetAssessment1.getId());
        assertThat(fixedAssetAssessment1).isEqualTo(fixedAssetAssessment2);
        fixedAssetAssessment2.setId(2L);
        assertThat(fixedAssetAssessment1).isNotEqualTo(fixedAssetAssessment2);
        fixedAssetAssessment1.setId(null);
        assertThat(fixedAssetAssessment1).isNotEqualTo(fixedAssetAssessment2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FixedAssetAssessmentDTO.class);
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO1 = new FixedAssetAssessmentDTO();
        fixedAssetAssessmentDTO1.setId(1L);
        FixedAssetAssessmentDTO fixedAssetAssessmentDTO2 = new FixedAssetAssessmentDTO();
        assertThat(fixedAssetAssessmentDTO1).isNotEqualTo(fixedAssetAssessmentDTO2);
        fixedAssetAssessmentDTO2.setId(fixedAssetAssessmentDTO1.getId());
        assertThat(fixedAssetAssessmentDTO1).isEqualTo(fixedAssetAssessmentDTO2);
        fixedAssetAssessmentDTO2.setId(2L);
        assertThat(fixedAssetAssessmentDTO1).isNotEqualTo(fixedAssetAssessmentDTO2);
        fixedAssetAssessmentDTO1.setId(null);
        assertThat(fixedAssetAssessmentDTO1).isNotEqualTo(fixedAssetAssessmentDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(fixedAssetAssessmentMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(fixedAssetAssessmentMapper.fromId(null)).isNull();
    }
}
