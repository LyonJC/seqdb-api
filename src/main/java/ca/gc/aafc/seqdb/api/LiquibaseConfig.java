package ca.gc.aafc.seqdb.api;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

/**
 * Liquibase configurations. Currently this class is responsible for changing the number of
 * connections in the liquibase connection pool.
 * 
 * The reason this is being configured programmically is because liquibase will use a different
 * DataSource so it cannot be configured using the hikari MaximumPoolSize property.
 */
@Configuration
@EnableConfigurationProperties({LiquibaseProperties.class, DataSourceProperties.class})
public class LiquibaseConfig {
 
  @Value("${spring.liquibase.maximumPoolSize:2}")
  private int maximumPoolSize;
  
  @Inject
  private DataSourceProperties dataSourceProperties;
  
  /**
   * Reconfigure the SpringLiquibase bean to set the connection pool size to a smaller number.
   * 
   * @param dataSource
   *          DataSource to retrieve the URL from.
   * @param liquibaseProperties
   *          Liquibase Properties to retrieve the user and password from.
   * @return Reconfigured SpringLiquibase bean.
   */
  @Bean
  public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {

    DataSource liquibaseDataSource = DataSourceBuilder.create()
        .username(liquibaseProperties.getUser())
        .password(liquibaseProperties.getPassword())
        .url(dataSourceProperties.getUrl())
        .build();
    
    // Change the pool size of the liquibase connection pool.
    if (liquibaseDataSource instanceof HikariDataSource) {
      ((HikariDataSource) liquibaseDataSource).setMaximumPoolSize(maximumPoolSize);
    }

    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog(liquibaseProperties.getChangeLog());
    liquibase.setDataSource(liquibaseDataSource);
    
    return liquibase;
  }

}
