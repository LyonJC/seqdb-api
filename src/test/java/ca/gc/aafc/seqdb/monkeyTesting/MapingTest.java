package ca.gc.aafc.seqdb.monkeyTesting;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import ca.gc.aafc.seqdb.api.dto.GroupDto;
import ca.gc.aafc.seqdb.api.dto.PcrPrimerDto;
import ca.gc.aafc.seqdb.api.dto.ThermocyclerProfileDto;
import ca.gc.aafc.seqdb.api.modelMapper.SeqdbModelMapper;
import ca.gc.aafc.seqdb.entities.Group;
import ca.gc.aafc.seqdb.entities.PcrProfile;
import ca.gc.aafc.seqdb.entities.PcrPrimer;
import ca.gc.aafc.seqdb.factories.PcrPrimerFactory;
import ca.gc.aafc.seqdb.factories.PcrProfileFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MapingTest {
  
  private static final ModelMapper modelMapper = SeqdbModelMapper.getConfiguredMapper();
      
  /**
   * Compares the given PcrProfile with it's expected values.
   * @param profileFromDto
   */
  private void validateCreatedPcrProfileSteps(PcrProfile profileFromDto) {
    assertEquals(profileFromDto.getStep1(), "Problems");
    
    assertEquals(profileFromDto.getStep2(), "worthy");

    assertEquals(profileFromDto.getStep3(), "of");

    assertEquals(profileFromDto.getStep4(), "attack");

    assertEquals(profileFromDto.getStep5(), "prove");

    assertEquals(profileFromDto.getStep6(), "their");

    assertEquals(profileFromDto.getStep7(), "worth");

    assertEquals(profileFromDto.getStep8(), "by");

    assertEquals(profileFromDto.getStep9(), "hitting");

    assertEquals(profileFromDto.getStep10(), "back");
    
    assertEquals(profileFromDto.getStep11(), "boom");
    
    assertEquals(profileFromDto.getStep12(), "Got'em");
    
    assertEquals(profileFromDto.getStep13(), null);
    
    assertEquals(profileFromDto.getStep14(), null);
    
    assertEquals(profileFromDto.getStep15(), "last");
  }
  
  @Test
  public void automaticMappingTest() {
    
    //generate a Dto and set it's values
    GroupDto groupDto = new GroupDto();
    groupDto.setDescription("Monkeys");
    groupDto.setGroupName("Pan");
    
    //create a entity from the Dto using modelMapper
    Group groupEntity = modelMapper.map(groupDto, Group.class);
    
    //Test that the values set onto the Dto are passed as expected
    assertEquals(groupDto.getDescription(), groupEntity.getDescription());
    assertEquals(groupDto.getGroupName(), groupEntity.getGroupName());
    
    //Change the entity's values
    groupEntity.setDescription("Pan");
    groupEntity.setGroupName("Simians");
    
    //Create a Dto from new entity
    groupDto = modelMapper.map(groupEntity, GroupDto.class);
    
    assertEquals(groupDto.getDescription(), groupEntity.getDescription());
    assertEquals(groupDto.getGroupName(), groupEntity.getGroupName());
   
  }
  
  @Test
  public void updateDtoTest() {
    GroupDto groupDto = new GroupDto();
    groupDto.setDescription("Monkeys");
    groupDto.setGroupName("Pan");
    
    PcrPrimerDto primerDto = new PcrPrimerDto();
    primerDto.setGroup(groupDto);
    
    Group group = new Group("baseName");
    PcrPrimer primer = PcrPrimerFactory.newPcrPrimer().group(group).build();
    System.out.println(primer);
    modelMapper.map(primerDto, primer);
    
    System.out.println(primer);
    
    
  }
  
  @Test
  public void configuredMappingTest() {
    
    //Create a new pcrProfile entity
    PcrProfile pcrProfile = PcrProfileFactory.newPcrProfile()
        .step1("Problems")
        .step2("worthy")
        .step3("of")
        .step4("attack")
        .step5("prove")
        .step6("their")
        .step7("worth")
        .step8("by")
        .step9("hitting")
        .step10("back")
        .step11("boom")
        .step12("Got'em")
        .step15("last").build();
    
    //Create a Dto using the just made pcrProfile entity 
    ThermocyclerProfileDto thermoProfileDto = modelMapper.map(pcrProfile, ThermocyclerProfileDto.class);
    
    Map<Integer, String> expectedMap = new HashMap<Integer, String>();
    expectedMap.put(1,"Problems" );
    expectedMap.put(2, "worthy");
    expectedMap.put(3, "of" );
    expectedMap.put(4,"attack");
    expectedMap.put(5, "prove");
    expectedMap.put(6, "their");
    expectedMap.put(7, "worth");
    expectedMap.put(8, "by");
    expectedMap.put(9, "hitting");
    expectedMap.put(10, "back");
    expectedMap.put(11,"boom" );
    expectedMap.put(12, "Got'em");
    expectedMap.put(13, null);
    expectedMap.put(14, null);
    expectedMap.put(15, "last");
    
    //Test that the values were mapped as expected
    assertEquals(thermoProfileDto.getSteps(), expectedMap);
    
    //Same thing but reversed.
    PcrProfile profileFromDto = modelMapper.map(thermoProfileDto, PcrProfile.class);
    
    validateCreatedPcrProfileSteps(profileFromDto);
    
  }
  

}
