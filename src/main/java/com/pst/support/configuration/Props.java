package com.pst.support.configuration;

import java.io.Console;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Classe permettant de récuperer les variables définis dans le fichier
 * application.properties
 */
@Configuration
public class Props {

	static final Logger LOGGER = Logger.getLogger(Props.class.toString());


	@Value("${DATABASE_HOST}")
	private String dbHost;

	@Value("${DATABASE_PORT}")
	private String dbPort;
	
	@Value("${DATABASE_NAME}")
	private String dbName;

	private String dbUsername;

	private String dbPwd;

	@Profile("prod")
	@Bean
	public DataSource dataSourceProd() {
		HikariConfig config = new HikariConfig();
		
		Console console = System.console();

		dbUsername = new String(console.readLine("username for db: "));
        dbPwd = new String(console.readPassword("pasword for %s: ", dbUsername));

		var dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUsername + "&password="
				+ dbPwd;

		config.setJdbcUrl(dbUrl);

		return new HikariDataSource(config);
	}
	
	
	@Profile("default")
	@Bean
	public DataSource dataSourceDefault() {
		HikariConfig config = new HikariConfig();
				
		var dbUrl = "jdbc:postgresql://localhost:5432/support_db?user=postgres&password=root";

		config.setJdbcUrl(dbUrl);

		return new HikariDataSource(config);
	}
}