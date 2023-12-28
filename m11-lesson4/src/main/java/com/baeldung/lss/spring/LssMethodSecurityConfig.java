package com.baeldung.lss.spring;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableMethodSecurity
public class LssMethodSecurityConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String dataSourceURL;

    @Value("${spring.datasource.username}")
    private String dataSourceUserName;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(aclPermissionEvaluator());
        return expressionHandler;
    }

    // ======= ACL configurations =======

    @Bean
    public AclPermissionEvaluator aclPermissionEvaluator() {
        final AclPermissionEvaluator aclPermissionEvaluator = new AclPermissionEvaluator(aclService());
        return aclPermissionEvaluator;
    }

    @Bean
    public JdbcMutableAclService aclService() {
        final JdbcMutableAclService service = new JdbcMutableAclService(dataSource(), lookupStrategy(), aclCache());
        // Those two line for MySQL only
        service.setClassIdentityQuery("SELECT @@IDENTITY");
        service.setSidIdentityQuery("SELECT @@IDENTITY");
        return service;
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource(), aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
    }

    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(cacheManager().getCache("aclCache"),
                permissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    @Bean
    public CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager();

        MutableConfiguration<Object, Object> mutableConfiguration = new MutableConfiguration<>();
        mutableConfiguration.setStoreByValue(false);  // otherwise value has to be Serializable
        cacheManager.createCache("aclCache", mutableConfiguration);

        return new JCacheCacheManager(cacheManager);
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSourceURL);
        dataSource.setUsername(dataSourceUserName);
        dataSource.setPassword(dataSourcePassword);

        return dataSource;
    }
}