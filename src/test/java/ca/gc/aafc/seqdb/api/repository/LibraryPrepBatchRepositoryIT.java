package ca.gc.aafc.seqdb.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.seqdb.api.dto.ContainerTypeDto;
import ca.gc.aafc.seqdb.api.dto.LibraryPrepBatchDto;
import ca.gc.aafc.seqdb.api.dto.ProductDto;
import ca.gc.aafc.seqdb.api.dto.ProtocolDto;
import ca.gc.aafc.seqdb.api.entities.ContainerType;
import ca.gc.aafc.seqdb.api.entities.Product;
import ca.gc.aafc.seqdb.api.entities.Protocol;
import ca.gc.aafc.seqdb.api.entities.libraryprep.LibraryPrepBatch;
import ca.gc.aafc.seqdb.api.testsupport.factories.ContainerTypeFactory;
import ca.gc.aafc.seqdb.api.testsupport.factories.LibraryPrepBatchFactory;
import ca.gc.aafc.seqdb.api.testsupport.factories.ProductFactory;
import ca.gc.aafc.seqdb.api.testsupport.factories.ProtocolFactory;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;

public class LibraryPrepBatchRepositoryIT extends BaseRepositoryTest {

  private static String TEST_NOTES = "test notes";

  private ContainerType testContainerType;
  private LibraryPrepBatch testBatch;
  private Product testProduct;
  private Protocol testProtocol;

  @Inject
  private ResourceRepository<LibraryPrepBatchDto, Serializable> libraryPrepBatchRepository;
  
  @Inject
  private ResourceRepository<ContainerTypeDto, Serializable> ctRepository;
  
  @Inject
  private ResourceRepository<ProductDto, Serializable> productRepository;
  
  @Inject
  private ResourceRepository<ProtocolDto, Serializable> protocolRepository;

  private LibraryPrepBatch createTestBatch() {
    testContainerType = ContainerTypeFactory.newContainerType()
        .build();
    
    persist(testContainerType);
    
    testProduct = ProductFactory.newProduct().build();
    persist(testProduct);
    
    testProtocol = ProtocolFactory.newProtocol().build();
    persist(testProtocol);
    
    testBatch = LibraryPrepBatchFactory.newLibraryPrepBatch()
        .name("test batch")
        .containerType(testContainerType)
        .product(testProduct)
        .protocol(testProtocol)
        .notes(TEST_NOTES)
        .build();
    
    persist(testBatch);
    
    return testBatch;
  }

  @BeforeEach
  public void setup() {
    createTestBatch();
  }

  @Test
  public void findBatch_whenBatchExists_batchReturned() {
    LibraryPrepBatchDto dto = libraryPrepBatchRepository.findOne(
        testBatch.getId(),
        new QuerySpec(LibraryPrepBatchDto.class)
    );
    
    assertNotNull(dto);
    assertEquals(TEST_NOTES, testBatch.getNotes());
  }
  
  @Test
  public void createBatch_onSuccess_batchCreated() {
    LibraryPrepBatchDto newDto = new LibraryPrepBatchDto();
    newDto.setName("new test batch");
    newDto.setNotes("notes");
    newDto.setCleanUpNotes("cleanup notes");
    newDto.setYieldNotes("yield notes");
    newDto.setContainerType(
        ctRepository.findOne(
            testContainerType.getId(),
            new QuerySpec(ContainerTypeDto.class)
        )
     );
    newDto.setProduct(
        productRepository.findOne(
            testProduct.getId(),
            new QuerySpec(ProductDto.class)
        )
    );
    newDto.setProtocol(
        protocolRepository.findOne(
            testProtocol.getId(),
            new QuerySpec(ProtocolDto.class)
        )
    );
    
    LibraryPrepBatchDto created = libraryPrepBatchRepository.create(newDto);
    
    assertNotNull(created.getLibraryPrepBatchId());
    assertEquals("notes", created.getNotes());
    assertEquals("cleanup notes", created.getCleanUpNotes());
    assertEquals("yield notes", created.getYieldNotes());
    assertEquals(testProduct.getProductId(), created.getProduct().getProductId());
    assertEquals(testProtocol.getProtocolId(), created.getProtocol().getProtocolId());
  }
  
  @Test
  public void updateBatch_onSuccess_batchUpdated() {
    LibraryPrepBatchDto dto = libraryPrepBatchRepository.findOne(
        testBatch.getId(),
        new QuerySpec(LibraryPrepBatchDto.class)
    );
    
    dto.setNotes("updated notes");
    libraryPrepBatchRepository.save(dto);
    assertEquals("updated notes", testBatch.getNotes());
  }
  
  @Test
  public void deleteBatch_onSuccess_batchDeleted() {
    libraryPrepBatchRepository.delete(testBatch.getId());
    assertNull(entityManager.find(LibraryPrepBatch.class, testBatch.getId()));
  }

}
