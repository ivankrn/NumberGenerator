package ru.ivankrn.numbergenerator.infrastructure.config.database;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfiguration {

    private final DatasourceProperties datasourceProperties;

    public HibernateConfiguration(DatasourceProperties datasourceProperties) {
        this.datasourceProperties = datasourceProperties;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setJpaProperties(hibernateProperties());
        em.setPackagesToScan("ru.ivankrn.numbergenerator");
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(jpaVendorAdapter);
        return em;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(datasourceProperties.getJdbcUrl());
        dataSource.setDriverClassName(datasourceProperties.getDriverClassName());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "500");
        hibernateProperties.setProperty("hibernate.order_inserts", "true");
        hibernateProperties.setProperty("hibernate.order_updates", "true");
        return hibernateProperties;
    }

    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
