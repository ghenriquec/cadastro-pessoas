package com.elotech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;

@SpringBootApplication
public class CadastroPessoasApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication app = new SpringApplication(CadastroPessoasApplication.class);
		app.setDefaultProperties(PropertiesLoaderUtils.loadAllProperties("application-private.properties"));
		app.run(args);
	}

}
