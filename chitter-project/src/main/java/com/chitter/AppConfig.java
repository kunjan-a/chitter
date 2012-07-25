package com.chitter;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

@Configuration
public class AppConfig {
    @Bean
    public SimpleJdbcTemplate simpleJdbcTemplate() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql:todo");      //This daatabase should already exist.
        dataSource.setUsername("postgres");
        dataSource.setPassword("directi");
        dataSource.setDriverClassName("org.postgresql.Driver");
        SimpleJdbcTemplate db = new SimpleJdbcTemplate(dataSource);
        return db;
    }

    @Bean
    public ThreadLocal<Long> userID() {
        return new ThreadLocal<Long>();
    }
}
