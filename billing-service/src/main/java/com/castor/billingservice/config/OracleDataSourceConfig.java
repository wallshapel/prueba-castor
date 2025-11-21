package com.castor.billingservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuración del DataSource de Oracle.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.castor.billingservice.repositories.invoice", // repos de Oracle
        entityManagerFactoryRef = "oracleEntityManagerFactory",
        transactionManagerRef = "oracleTransactionManager"
)
public class OracleDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.oracle")
    public DataSourceProperties oracleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource oracleDataSource(
            @Qualifier("oracleDataSourceProperties") DataSourceProperties properties
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
            @Qualifier("oracleDataSource") DataSource oracleDataSource
    ) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(oracleDataSource);
        factory.setPackagesToScan("com.castor.billingservice.entities.invoice"); // entidades Oracle
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", "none");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        factory.setJpaProperties(jpaProps);

        return factory;
    }

    @Bean
    public PlatformTransactionManager oracleTransactionManager(
            @Qualifier("oracleEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }

    @Bean
    public JdbcTemplate oracleJdbcTemplate(
            @Qualifier("oracleDataSource") DataSource oracleDataSource
    ) {
        return new JdbcTemplate(oracleDataSource);
    }
}
