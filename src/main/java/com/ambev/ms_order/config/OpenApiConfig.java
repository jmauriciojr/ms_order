package com.ambev.ms_order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Value("${api.documentation.team.email}")
	private String email;
	
	@Value("${api.documentation.team.url}")
	private String url;
	
	@Value("${api.documentation.team.name}")
	private String team;

	@Value("${api.documentation.title}")
	private String title;
	
	@Value("${api.documentation.description}")
	private String description;
	
	@Value("${api.documentation.version}")
	private String version;

	@Bean
	OpenAPI usersMicroserviceOpenAPI() {
		var contact = new Contact().name(team).email(email).url(url);
		
		return new OpenAPI().info(new Info()
				.title(title)
				.description(description)
				.contact(contact)
				.version(version));
	}

}
