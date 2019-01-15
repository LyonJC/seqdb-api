package ca.gc.aafc.seqdb.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.criteria.Path;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.seqdb.api.dto.ChainDto;
import ca.gc.aafc.seqdb.api.dto.ChainStepTemplateDto;
import ca.gc.aafc.seqdb.api.dto.ChainTemplateDto;
import ca.gc.aafc.seqdb.api.dto.GroupDto;
import ca.gc.aafc.seqdb.api.dto.PcrBatchDto;
import ca.gc.aafc.seqdb.api.dto.PcrPrimerDto;
import ca.gc.aafc.seqdb.api.dto.PcrReactionDto;
import ca.gc.aafc.seqdb.api.dto.RegionDto;
import ca.gc.aafc.seqdb.api.dto.StepResourceDto;
import ca.gc.aafc.seqdb.api.dto.StepTemplateDto;
import ca.gc.aafc.seqdb.api.repository.JpaDtoRepository;
import ca.gc.aafc.seqdb.api.repository.JpaRelationshipRepository;
import ca.gc.aafc.seqdb.api.repository.JpaResourceRepository;
import ca.gc.aafc.seqdb.api.repository.handlers.JpaDtoMapper;
import ca.gc.aafc.seqdb.api.repository.handlers.SimpleFilterHandler;
import ca.gc.aafc.seqdb.api.security.authorization.ReadableGroupFilterHandlerFactory;
import ca.gc.aafc.seqdb.entities.Chain;
import ca.gc.aafc.seqdb.entities.ChainStepTemplate;
import ca.gc.aafc.seqdb.entities.ChainTemplate;
import ca.gc.aafc.seqdb.entities.Group;
import ca.gc.aafc.seqdb.entities.PcrBatch;
import ca.gc.aafc.seqdb.entities.PcrPrimer;
import ca.gc.aafc.seqdb.entities.PcrReaction;
import ca.gc.aafc.seqdb.entities.Region;
import ca.gc.aafc.seqdb.entities.StepResource;
import ca.gc.aafc.seqdb.entities.StepTemplate;

@Configuration
@EntityScan("ca.gc.aafc.seqdb.entities")
public class ResourceRepositoryConfig {

  @Inject
  private SimpleFilterHandler simpleFilterHandler;
  
  @Inject
  private ReadableGroupFilterHandlerFactory groupFilterFactory;

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
    jpaEntities.put(PcrBatchDto.class, PcrBatch.class);
    jpaEntities.put(PcrReactionDto.class, PcrReaction.class);
    jpaEntities.put(GroupDto.class, Group.class);
    jpaEntities.put(ChainTemplateDto.class, ChainTemplate.class);
    jpaEntities.put(StepTemplateDto.class, StepTemplate.class);
    jpaEntities.put(ChainStepTemplateDto.class, ChainStepTemplate.class);
    jpaEntities.put(ChainDto.class, Chain.class);
    jpaEntities.put(StepResourceDto.class, StepResource.class);

    return new JpaDtoMapper(jpaEntities);
  }

  @Bean
  public JpaResourceRepository<PcrPrimerDto> pcrPrimerRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrPrimerDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }

  @Bean
  public JpaResourceRepository<RegionDto> regionRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }

  @Bean
  public JpaResourceRepository<PcrBatchDto> pcrBatchRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrBatchDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }

  @Bean
  public JpaResourceRepository<PcrReactionDto> pcrReactionRepository(
      JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrReactionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("pcrBatch").get("group"))
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<GroupDto> groupRepository(
      JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        GroupDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> (Path<Group>) root)
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<ChainTemplateDto> chainTemplateRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        ChainTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<StepTemplateDto> stepTemplateRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        StepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<ChainStepTemplateDto> chainStepTemplateRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        ChainStepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<ChainDto> chainRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        ChainDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }
  
  @Bean
  public JpaResourceRepository<StepResourceDto> stepResourceRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        StepResourceDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
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
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }

  @Bean
  public JpaRelationshipRepository<PcrBatchDto, PcrReactionDto> pcrBatchToPcrReactionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrBatchDto.class,
        PcrReactionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("pcrBatch").get("group"))
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<PcrBatchDto, GroupDto> pcrBatchToGroupRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrBatchDto.class,
        GroupDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> (Path<Group>) root)
            )
        );
  }

  @Bean
  public JpaRelationshipRepository<PcrReactionDto, PcrBatchDto> pcrReactionToPcrBatchRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrReactionDto.class,
        PcrBatchDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<ChainDto, ChainTemplateDto> chainToChainTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainDto.class,
        ChainTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<ChainStepTemplateDto, ChainTemplateDto> chainStepTemplateToChainTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainStepTemplateDto.class,
        ChainTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<ChainStepTemplateDto, StepTemplateDto> chainStepTemplateToStepTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ChainStepTemplateDto.class,
        StepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<StepResourceDto, ChainStepTemplateDto> stepResourceToChainStepTemplateRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        ChainStepTemplateDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<StepResourceDto, ChainDto> stepResourceToChainRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        ChainDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<StepResourceDto, RegionDto> stepResourceToRegionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }
  
  @Bean
  public JpaRelationshipRepository<StepResourceDto, PcrPrimerDto> stepResourceToPcrPrimerRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        StepResourceDto.class,
        PcrPrimerDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler
        )
    );
  }

}
