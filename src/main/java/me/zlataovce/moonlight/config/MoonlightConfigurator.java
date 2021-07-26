package me.zlataovce.moonlight.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MoonlightConfigurator {
    private MoonlightConfigurationManager moonlightConfigurationManager;

    @Autowired
    public void setConfigurationManager(MoonlightConfigurationManager moonlightConfigurationManager) {
        this.moonlightConfigurationManager = moonlightConfigurationManager;
    }

    @Bean
    public MoonlightConfigurationManager configurationManager() {
        return new MoonlightConfigurationManager();
    }

    @Bean
    public DataSource getDataSource() {
        final String jdbcUrl = (System.getenv("moonlightjdbcurl") != null) ? System.getenv("moonlightjdbcurl") : this.moonlightConfigurationManager.getProp().getProperty("jdbcurl");
        final String dbuser = (System.getenv("moonlightdbuser") != null) ? System.getenv("moonlightdbuser") : this.moonlightConfigurationManager.getProp().getProperty("dbuser");
        final String dbpassword = (System.getenv("moonlightdbpassword") != null) ? System.getenv("moonlightdbpassword") : this.moonlightConfigurationManager.getProp().getProperty("dbpassword");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(jdbcUrl)
                .username(dbuser)
                .password(dbpassword)
                .build();
    }
}
