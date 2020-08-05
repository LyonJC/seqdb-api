package ca.gc.aafc.seqdb.api.repository;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import ca.gc.aafc.seqdb.api.dto.PcrBatchDto;
import ca.gc.aafc.seqdb.api.repository.filter.RsqlFilterHandler;
import ca.gc.aafc.seqdb.api.repository.filter.SimpleFilterHandler;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaDtoRepository;
import ca.gc.aafc.seqdb.api.repository.jpa.JpaResourceRepository;
import ca.gc.aafc.seqdb.api.repository.meta.JpaMetaInformationProvider;


@Component
public class PcrBatchRepository extends JpaResourceRepository<PcrBatchDto> {

  public PcrBatchRepository(JpaDtoRepository dtoRepository, SimpleFilterHandler simpleFilterHandler,
      RsqlFilterHandler rsqlFilterHandler, 
      JpaMetaInformationProvider metaInformationProvider) {
    super(PcrBatchDto.class, dtoRepository,
        Arrays.asList(simpleFilterHandler, rsqlFilterHandler),
        metaInformationProvider);
  }

}
