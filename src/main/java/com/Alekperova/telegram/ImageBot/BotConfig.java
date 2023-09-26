package com.Alekperova.telegram.ImageBot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;


@Configuration
@Data
@PropertySource("config/bot.properties")
public class BotConfig {

    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String token;
    @Value("${bot.path}") String path;
}

