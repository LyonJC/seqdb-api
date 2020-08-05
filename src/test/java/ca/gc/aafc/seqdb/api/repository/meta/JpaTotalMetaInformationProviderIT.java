package ca.gc.aafc.seqdb.api.repository.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.seqdb.api.dto.PcrBatchDto;
import ca.gc.aafc.seqdb.api.dto.PcrReactionDto;
import ca.gc.aafc.seqdb.api.dto.RegionDto;
import ca.gc.aafc.seqdb.api.repository.BaseRepositoryTest;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaRelationshipRepository;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaResourceRepository;
import ca.gc.aafc.seqdb.api.entities.PcrBatch;
import ca.gc.aafc.seqdb.api.entities.Region;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.core.resource.meta.DefaultPagedMetaInformation;

public class JpaTotalMetaInformationProviderIT extends BaseRepositoryTest {

  @Inject
  private JpaResourceRepository<RegionDto> regionRepository;
  
  @Inject
  private JpaRelationshipRepository<PcrBatchDto, PcrReactionDto> pcrBatchToReactionRepository;
  
  private static final int NUMBER_OF_REGION_TO_CREATE = 11; 
  
  /**
   * Persist example data for these tests.
   */
  @BeforeEach
  public void persistTestRegions() {
    for (int i = 1; i <= NUMBER_OF_REGION_TO_CREATE; i++) {
      Region region = new Region();
      region.setName("test region " + i);
      // region.setDescription("desc");
      region.setSymbol("symbol");
      entityManager.persist(region);
    }
  }
  
  @Test
  public void jpaResourceRepositoryFindAll_noAdditionalOptions_fullTotalIsIncluded() {
    ResourceList<RegionDto> regions = regionRepository.findAll(new QuerySpec(RegionDto.class));
    DefaultPagedMetaInformation meta = (DefaultPagedMetaInformation) regions.getMeta();
    assertEquals(NUMBER_OF_REGION_TO_CREATE, meta.getTotalResourceCount().longValue());
  }
  
  @Test
  public void jpaResourceRepositoryFindAll_whenFilterIsAdded_reducedTotalIsIncluded() {
    QuerySpec querySpec = new QuerySpec(RegionDto.class);
    querySpec.addFilter(new FilterSpec(Arrays.asList("name"), FilterOperator.EQ, "test region 5"));
    
    ResourceList<RegionDto> regions = regionRepository.findAll(querySpec);
    DefaultPagedMetaInformation meta = (DefaultPagedMetaInformation) regions.getMeta();
    assertEquals(1, regions.size());
    assertEquals(1, meta.getTotalResourceCount().longValue());
  }
  
  @Test
  public void jpaRelationshipRepository_whenFilterIsAdded_reducedTotalIsIncluded() {
    PcrBatch testBatch = persistTestPcrBatchWith22Reactions("test-batch");
    
    QuerySpec querySpec = new QuerySpec(PcrReactionDto.class);
    querySpec.addFilter(new FilterSpec(Arrays.asList("tubeNumber"), FilterOperator.EQ, 5));
    ResourceList<PcrReactionDto> reactions = pcrBatchToReactionRepository
        .findManyTargets(testBatch.getPcrBatchId(), "reactions", querySpec);
    DefaultPagedMetaInformation meta = (DefaultPagedMetaInformation) reactions.getMeta();
    
    assertEquals(1, reactions.size());
    assertEquals(1, meta.getTotalResourceCount().longValue());
  }
  
  @Test
  public void jpaResourceRepositoryFindAll_whenPageLimitIsSpecified_fullTotalIsIncluded() {
    QuerySpec querySpec = new QuerySpec(RegionDto.class);
    querySpec.setLimit(1L);

    ResourceList<RegionDto> regions = regionRepository.findAll(querySpec);
    DefaultPagedMetaInformation meta = (DefaultPagedMetaInformation) regions.getMeta();
    assertEquals(1, regions.size());
    assertEquals(NUMBER_OF_REGION_TO_CREATE, meta.getTotalResourceCount().longValue());
  }
  
}
