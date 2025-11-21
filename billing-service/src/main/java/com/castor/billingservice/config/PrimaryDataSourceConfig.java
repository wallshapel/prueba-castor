package com.castor.billingservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuración del DataSource principal (PostgreSQL).
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.castor.billingservice.repositories.customer", // repos de PostgreSQL
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource primaryDataSource(
            @Qualifier("primaryDataSourceProperties") DataSourceProperties properties
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource primaryDataSource
    ) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(primaryDataSource);
        factory.setPackagesToScan("com.castor.billingservice.entities.customer"); // entidades PostgreSQL
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        factory.setJpaProperties(jpaProps);

        return factory;
    }

    @Bean
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }
}
