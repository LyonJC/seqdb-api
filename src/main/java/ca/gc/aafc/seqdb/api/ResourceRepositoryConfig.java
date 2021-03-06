package ca.gc.aafc.seqdb.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import ca.gc.aafc.seqdb.api.dto.ChainDto;
import ca.gc.aafc.seqdb.api.dto.ChainStepTemplateDto;
import ca.gc.aafc.seqdb.api.dto.ChainTemplateDto;
import ca.gc.aafc.seqdb.api.dto.ContainerTypeDto;
import ca.gc.aafc.seqdb.api.dto.IndexSetDto;
import ca.gc.aafc.seqdb.api.dto.LibraryPoolContentDto;
import ca.gc.aafc.seqdb.api.dto.LibraryPoolDto;
import ca.gc.aafc.seqdb.api.dto.LibraryPrepBatchDto;
import ca.gc.aafc.seqdb.api.dto.LibraryPrepDto;
import ca.gc.aafc.seqdb.api.dto.NgsIndexDto;
import ca.gc.aafc.seqdb.api.dto.PcrPrimerDto;
import ca.gc.aafc.seqdb.api.dto.PreLibraryPrepDto;
import ca.gc.aafc.seqdb.api.dto.ProductDto;
import ca.gc.aafc.seqdb.api.dto.ProtocolDto;
import ca.gc.aafc.seqdb.api.dto.RegionDto;
import ca.gc.aafc.seqdb.api.dto.SampleDto;
import ca.gc.aafc.seqdb.api.dto.StepResourceDto;
import ca.gc.aafc.seqdb.api.dto.StepTemplateDto;
import ca.gc.aafc.seqdb.api.dto.ThermocyclerProfileDto;
import ca.gc.aafc.seqdb.api.entities.ContainerType;
import ca.gc.aafc.seqdb.api.entities.PcrPrimer;
import ca.gc.aafc.seqdb.api.entities.PcrProfile;
import ca.gc.aafc.seqdb.api.entities.PreLibraryPrep;
import ca.gc.aafc.seqdb.api.entities.Product;
import ca.gc.aafc.seqdb.api.entities.Protocol;
import ca.gc.aafc.seqdb.api.entities.Region;
import ca.gc.aafc.seqdb.api.entities.Sample;
import ca.gc.aafc.seqdb.api.entities.libraryprep.IndexSet;
import ca.gc.aafc.seqdb.api.entities.libraryprep.LibraryPrep;
import ca.gc.aafc.seqdb.api.entities.libraryprep.LibraryPrepBatch;
import ca.gc.aafc.seqdb.api.entities.libraryprep.NgsIndex;
import ca.gc.aafc.seqdb.api.entities.pooledlibraries.LibraryPool;
import ca.gc.aafc.seqdb.api.entities.pooledlibraries.LibraryPoolContent;
import ca.gc.aafc.seqdb.api.entities.workflow.Chain;
import ca.gc.aafc.seqdb.api.entities.workflow.ChainStepTemplate;
import ca.gc.aafc.seqdb.api.entities.workflow.ChainTemplate;
import ca.gc.aafc.seqdb.api.entities.workflow.StepResource;
import ca.gc.aafc.seqdb.api.entities.workflow.StepTemplate;
import ca.gc.aafc.seqdb.api.repository.VocabularyReadOnlyRepository;
import ca.gc.aafc.seqdb.api.repository.filter.RsqlFilterHandler;
import ca.gc.aafc.seqdb.api.repository.filter.SimpleFilterHandler;
import ca.gc.aafc.seqdb.api.repository.handlers.JpaDtoMapper;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaDtoRepository;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaRelationshipRepository;
import ca.gc.aafc.seqdb.api.repository.meta.JpaTotalMetaInformationProvider;
import io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper;
import io.crnk.operations.server.OperationsModule;
import io.crnk.operations.server.TransactionOperationFilter;
import io.crnk.spring.jpa.SpringTransactionRunner;

@Configuration
//Restricted to repository package so it won't affect tests with bean mocking/overriding.
@ComponentScan("ca.gc.aafc.seqdb.api.repository")
@EntityScan("ca.gc.aafc.seqdb.api.entities")
// Must explicitly depend on "querySpecUrlMapper" so Spring can inject it into this class'
// initQuerySpecUrlMapper method.
@DependsOn("querySpecUrlMapper")
public class ResourceRepositoryConfig {

  @Inject
  private SimpleFilterHandler simpleFilterHandler;
  
  @Inject
  private RsqlFilterHandler rsqlFilterHandler;
  
  @Inject
  private JpaTotalMetaInformationProvider metaInformationProvider;
  
  @Inject
  public void initQuerySpecUrlMapper(DefaultQuerySpecUrlMapper mapper) {
    // Disables Crnk's behavior of splitting up query params that contain commas into HashSets.
    // This will allow RSQL 'OR' filters like "name==primer2,name==primer4".
    mapper.setAllowCommaSeparatedValue(false);
  }
  
  /**
   * Configures DTO-to-Entity mappings.
   * 
   * @return the DtoJpaMapper
   */
  @Bean
  public JpaDtoMapper dtoJpaMapper() {
    Map<Class<?>, Class<?>> jpaEntities = new HashMap<>();

    jpaEntities.put(RegionDto.class, Region.class);
    jpaEntities.put(PcrPrimerDto.class, PcrPrimer.class);
    jpaEntities.put(ChainTemplateDto.class, ChainTemplate.class);
    jpaEntities.put(StepTemplateDto.class, StepTemplate.class);
    jpaEntities.put(ChainStepTemplateDto.class, ChainStepTemplate.class);
    jpaEntities.put(ChainDto.class, Chain.class);
    jpaEntities.put(StepResourceDto.class, StepResource.class);
    jpaEntities.put(ThermocyclerProfileDto.class, PcrProfile.class);
    jpaEntities.put(ProductDto.class, Product.class);
    jpaEntities.put(ProtocolDto.class, Protocol.class);
    jpaEntities.put(SampleDto.class, Sample.class);
    jpaEntities.put(PreLibraryPrepDto.class, PreLibraryPrep.class);
    jpaEntities.put(LibraryPrepBatchDto.class, LibraryPrepBatch.class);
    jpaEntities.put(LibraryPrepDto.class, LibraryPrep.class);
    jpaEntities.put(ContainerTypeDto.class, ContainerType.class);
    jpaEntities.put(IndexSetDto.class, IndexSet.class);
    jpaEntities.put(NgsIndexDto.class, NgsIndex.class);
    jpaEntities.put(LibraryPoolDto.class, LibraryPool.class);
    jpaEntities.put(LibraryPoolContentDto.class, LibraryPoolContent.class);
    
    return new JpaDtoMapper(jpaEntities);
  }
  
  /**
   * Registers the transaction filter that executes a transaction around bulk jsonpatch operations.
   * 
   * @param module
   *          the Crnk operations module.
   */
  @Inject
  public void initTransactionOperationFilter(OperationsModule module) {
    module.addFilter(new TransactionOperationFilter());
    module.setIncludeChangedRelationships(false);
  }
  
  /**
   * Provides Crnk's SpringTransactionRunner that implements transactions around bulk jsonpatch
   * operations using Spring's transaction management.
   * 
   * @return the transaction runner.
   */
  @Bean
  public SpringTransactionRunner crnkSpringTransactionRunner() {
    return new SpringTransactionRunner();
  }

  @Bean
  public JpaTotalMetaInformationProvider metaInformationProvider(EntityManager entityManager) {
    return new JpaTotalMetaInformationProvider(entityManager, dtoJpaMapper());
  }
  
  @Bean
  public VocabularyReadOnlyRepository vocabularyDto(){
    return new VocabularyReadOnlyRepository();
  }
  
  @Bean
  public JpaRelationshipRepository<PcrPrimerDto, RegionDto> primerToRegionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrPrimerDto.class,
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaRelationshipRepository<ProtocolDto, ProductDto> protocolToProductRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ProtocolDto.class,
        ProductDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler, 
            rsqlFilterHandler
            ),
        metaInformationProvider
    );
  }  

  /**
   * Relationship Repository between a Chain and ChainTemplate.
   */
  @Bean
  public JpaRelationshipRepository<ChainDto, ChainTemplateDto> chainToChainTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainDto.class,
        ChainTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a ChainStepTemplate and ChainTemplate.
   */
  @Bean
  public JpaRelationshipRepository<ChainStepTemplateDto, ChainTemplateDto> chainStepTemplateToChainTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainStepTemplateDto.class,
        ChainTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a ChainStepTemplate and StepTemplate.
   */
  @Bean
  public JpaRelationshipRepository<ChainStepTemplateDto, StepTemplateDto> chainStepTemplateToStepTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainStepTemplateDto.class,
        StepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a StepResource and Chain.
   */
  @Bean
  public JpaRelationshipRepository<StepResourceDto, ChainDto> stepResourceToChainRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        ChainDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a StepResource and Region.
   */
  @Bean
  public JpaRelationshipRepository<StepResourceDto, RegionDto> stepResourceToRegionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a StepResource and PcrPrimer.
   */
  @Bean
  public JpaRelationshipRepository<StepResourceDto, PcrPrimerDto> stepResourceToPcrPrimerRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        PcrPrimerDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  /**
   * Relationship Repository between a StepResource and Sample.
   */
  @Bean
  public JpaRelationshipRepository<StepResourceDto, SampleDto> stepResourceToSampleRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        SampleDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<SampleDto, ProductDto> sampleToProductRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        SampleDto.class, 
        ProductDto.class, 
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler, 
            rsqlFilterHandler),
        metaInformationProvider);
  }
  
  @Bean
  public JpaRelationshipRepository<SampleDto, ProtocolDto> sampleToProtocolRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        SampleDto.class, 
        ProtocolDto.class, 
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler, 
            rsqlFilterHandler),
        metaInformationProvider);
  }
  
  /**
   * Relationship Repository between a StepResource and ChainStepTemplate.
   */
  @Bean
  public JpaRelationshipRepository<StepResourceDto, ChainStepTemplateDto> stepResourceToChainStepTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        ChainStepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<StepResourceDto, PreLibraryPrepDto> stepResourceToPreLibraryPrepRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        PreLibraryPrepDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<PreLibraryPrepDto, ProtocolDto> preLibraryPrepToProtocolRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PreLibraryPrepDto.class,
        ProtocolDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<PreLibraryPrepDto, ProductDto> preLibraryPrepToProductRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PreLibraryPrepDto.class,
        ProductDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<LibraryPrepBatchDto, LibraryPrepDto> libraryPrepBatchToLibraryPrepRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        LibraryPrepBatchDto.class,
        LibraryPrepDto.class,
        dtoRepository,
        Arrays.asList(
            rsqlFilterHandler
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaRelationshipRepository<IndexSetDto, NgsIndexDto> indexSetToNgsIndexesRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(IndexSetDto.class, NgsIndexDto.class, dtoRepository,
        Arrays.asList(rsqlFilterHandler), metaInformationProvider);
  }

  @Bean
  public JpaRelationshipRepository<LibraryPoolDto, LibraryPoolContentDto> libraryPoolToContentRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(LibraryPoolDto.class, LibraryPoolContentDto.class,
        dtoRepository, Arrays.asList(rsqlFilterHandler), metaInformationProvider);
  }

  @Bean
  public JpaRelationshipRepository<LibraryPoolContentDto, LibraryPoolDto> libraryPoolContentToPoolRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(LibraryPoolContentDto.class, LibraryPoolDto.class,
        dtoRepository, Arrays.asList(rsqlFilterHandler), metaInformationProvider);
  }
}
