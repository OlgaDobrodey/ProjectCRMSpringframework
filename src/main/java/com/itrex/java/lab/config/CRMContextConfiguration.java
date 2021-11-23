package com.itrex.java.lab.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@ComponentScan("com.itrex.java.lab")
@PropertySource("classpath:/application.properties")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Slf4j
public class CRMContextConfiguration {

    @Value("${database.url}")
    private String url;
    @Value("${database.user}")
    private String user;
    @Value("${database.password}")
    private String password;
    @Value("${database.migration.location}")
    private String migrationLocation;
    @Value("${database.schema}")
    private String schema;
    @Value("${entity.package.to.scan}")
    private String entityPackageScan;
    @Value("${database.driver}")
    private String driver;
    @Value("${hibernate.dialect.property}")
    private String dialect;
    @Value("${hibernate.show_sql.property}")
    private String showSql;
    @Value("${hibernate.format_sql.property}")
    private String formatSql;
    @Value("${logging.config.profile.debug}")
    private String loggingProfileDebug;
    @Value("${logging.config.profile.info}")
    private String loggingProfileInfo;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations(migrationLocation)
                .schemas(schema)
                .load();
    }

    @Bean
    @DependsOn("flyway")
    public JdbcConnectionPool jdbcConnectionPool() {
        return JdbcConnectionPool.create(url, user, password);
    }

    @Bean
    @DependsOn("flyway")
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass(driver);
        cpds.setJdbcUrl(url);
        cpds.setUser(user);
        cpds.setPassword(password);
        return cpds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() throws PropertyVetoException {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(entityPackageScan);

        Properties properties = new Properties();
        properties.setProperty(Environment.DIALECT, dialect);

        properties.setProperty(Environment.SHOW_SQL, showSql);
        properties.setProperty(Environment.FORMAT_SQL, formatSql);

        sessionFactory.setHibernateProperties(properties);

        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {

        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    @Bean
    @Profile("info_log")
    public void LoggerConfigDevProfile() {
        PropertyConfigurator.configure(loggingProfileInfo);
    }

    @Bean
    @Profile("debug_log")
    public void LoggerConfigProdProfile() {
        PropertyConfigurator.configure(loggingProfileDebug);
    }

}
