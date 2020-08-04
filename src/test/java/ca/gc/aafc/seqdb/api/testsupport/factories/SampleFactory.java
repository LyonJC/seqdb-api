package ca.gc.aafc.seqdb.api.testsupport.factories;

import java.util.List;
import java.util.function.BiFunction;

import ca.gc.aafc.seqdb.api.entities.Group;
import ca.gc.aafc.seqdb.api.entities.Sample;
import ca.gc.aafc.seqdb.api.entities.Sample.SampleBuilder;

/**
 * Creates Sample entities that are populated with all the required fields.
 */
public class SampleFactory implements TestableEntityFactory<Sample> {
  
  @Override
  public Sample getEntityInstance() {

    return newSample().build();

  }

  /**
   * Static method that can be called to return a configured builder that can
   * be further customized to return the actual entity object, call the
   * .build() method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Sample.SampleBuilder newSample() {

    return newSample(null);
  }

  /**
   * Static method that can be called to return a configured builder that can
   * be further customized to return the actual entity object, call the
   * .build() method on a builder,with specified group passed on as parameter
   * @param group Group to be set on the {@link Sample}
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Sample.SampleBuilder newSample(Group group) {
    
    Sample.SampleBuilder bldr = Sample.builder()
        .name(TestableEntityFactory.generateRandomName(10))
        .version(TestableEntityFactory.generateRandomNameLettersOnly(1))
        .group(group);
    
    return bldr;

  }
  
  
  /**
   * A utility method to create a list of qty number of Samples with no
   * configuration.
   * 
   * @param qty
   *            The number of Samples populated in the list
   * @return List of Samples
   */
  public static List<Sample> newListOf(int qty) {
    return newListOf(qty, null);
  }

  /**
   * A utility method to create a list of qty number of Sample with an
   * incrementing attribute based on the configuration argument. An example of
   * configuration would be the functional interface (bldr, index) ->
   * bldr.name(" string" + index)
   * 
   * @param qty
   *            The number of Sample that is populated in the list.
   * @param configuration
   *            the function to apply, usually to differentiate the different
   *            entities in the list.
   * @return List of Sample
   */
  public static List<Sample> newListOf(int qty,
      BiFunction<Sample.SampleBuilder, Integer, Sample.SampleBuilder> configuration) {

    return TestableEntityFactory.newEntity(qty, SampleFactory::newSample, configuration,
        SampleBuilder::build);
  }

}
