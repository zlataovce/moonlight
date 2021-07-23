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
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(this.moonlightConfigurationManager.getProp().getProperty("jdbcurl"))
                .username(this.moonlightConfigurationManager.getProp().getProperty("dbuser"))
                .password(this.moonlightConfigurationManager.getProp().getProperty("dbpassword"))
                .build();
    }
}
