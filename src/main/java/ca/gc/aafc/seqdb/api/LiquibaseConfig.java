package ca.gc.aafc.seqdb.api;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

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
  
  private static final int LIQUIBASE_POOL_SIZE = 2;
  
  @Autowired
  private LiquibaseProperties liquibaseProperties;
  
  @Autowired
  private DataSourceProperties dataSourceProperties;
  
  @Bean
  @Primary
  public DataSource dataSource() {
      return DataSourceBuilder.create()
              .username(dataSourceProperties.getUsername())
              .password(dataSourceProperties.getPassword())
              .url(dataSourceProperties.getUrl())
              .build();
  }

  @LiquibaseDataSource
  @Bean
  public DataSource liquibaseDataSource() {
      DataSource ds = DataSourceBuilder.create()
              .username(liquibaseProperties.getUser())
              .password(liquibaseProperties.getPassword())
              .url(dataSourceProperties.getUrl())
              .build();

      if (ds instanceof HikariDataSource) {
          ((HikariDataSource) ds).setMaximumPoolSize(LIQUIBASE_POOL_SIZE);
      }

      return ds;
  }
  
}
