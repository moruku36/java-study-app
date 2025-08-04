package com.studyapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url:}")
    private String databaseUrl;
    
    @Bean
    @Profile("!local")
    public DataSource dataSource() throws URISyntaxException {
        if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.contains("h2")) {
            URI dbUri = new URI(databaseUrl);
            
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnReturn(true);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setMaxActive(10);
            dataSource.setMinIdle(2);
            dataSource.setMaxIdle(5);
            
            return dataSource;
        }
        
        // ローカル環境用のH2データソースを返す
        org.apache.tomcat.jdbc.pool.DataSource h2DataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        h2DataSource.setDriverClassName("org.h2.Driver");
        h2DataSource.setUrl("jdbc:h2:mem:studydb");
        h2DataSource.setUsername("sa");
        h2DataSource.setPassword("");
        h2DataSource.setTestOnBorrow(true);
        h2DataSource.setTestWhileIdle(true);
        h2DataSource.setTestOnReturn(true);
        h2DataSource.setValidationQuery("SELECT 1");
        h2DataSource.setMaxActive(10);
        h2DataSource.setMinIdle(2);
        h2DataSource.setMaxIdle(5);
        
        return h2DataSource;
    }
} 