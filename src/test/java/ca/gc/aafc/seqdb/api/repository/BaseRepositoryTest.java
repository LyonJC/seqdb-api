package ca.gc.aafc.seqdb.api.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ca.gc.aafc.seqdb.api.BaseIntegrationTest;
import ca.gc.aafc.seqdb.entities.PcrPrimer;
import ca.gc.aafc.seqdb.entities.Region;
import ca.gc.aafc.seqdb.entities.PcrPrimer.PrimerType;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.core.queryspec.IncludeFieldSpec;
import io.crnk.core.queryspec.IncludeRelationSpec;

public abstract class BaseRepositoryTest extends BaseIntegrationTest {

  protected static final String TEST_PRIMER_NAME = "test primer";
  protected static final Integer TEST_PRIMER_LOT_NUMBER = 1;
  protected static final PrimerType TEST_PRIMER_TYPE = PrimerType.PRIMER;
  protected static final String TEST_PRIMER_SEQ = "test seq";
  
  protected static final String TEST_REGION_NAME = "test region";
  protected static final String TEST_REGION_DESCRIPTION = "test description";
  protected static final String TEST_REGION_SYMBOL = "test symbol";
  
  @Inject
  protected ResourceRegistry resourceRegistry;
  
  /**
   * Persists a PcrPrimer with the required fields set.
   *
   * @return the persisted primer
   */
  protected PcrPrimer persistTestPrimer() {
    PcrPrimer primer = new PcrPrimer();
    primer.setName(TEST_PRIMER_NAME);
    primer.setLotNumber(TEST_PRIMER_LOT_NUMBER);
    primer.setType(TEST_PRIMER_TYPE);
    primer.setSeq(TEST_PRIMER_SEQ);

    assertNull(primer.getId());
    entityManager.persist(primer);
    // New primer must have an ID.
    assertNotNull(primer.getId());

    return primer;
  }
  
  protected PcrPrimer persistTestPrimerWithRegion() {
    PcrPrimer primer = this.persistTestPrimer();
    
    Region region = new Region();
    region.setName(TEST_REGION_NAME);
    region.setDescription(TEST_REGION_DESCRIPTION);
    region.setSymbol(TEST_REGION_SYMBOL);
    
    assertNull(region.getId());
    entityManager.persist(region);
    assertNotNull(region.getId());
    
    primer.setRegion(region);
    
    return primer;
  }

  protected static List<IncludeFieldSpec> includeFieldSpecs(String... includedFields) {
    return Arrays.asList(includedFields)
        .stream()
        .map(Arrays::asList)
        .map(IncludeFieldSpec::new)
        .collect(Collectors.toList());
  }
  
  protected static List<IncludeRelationSpec> includeRelationSpecs(String... includedRelations) {
    return Arrays.asList(includedRelations)
        .stream()
        .map(Arrays::asList)
        .map(IncludeRelationSpec::new)
        .collect(Collectors.toList());
  }
  
}
