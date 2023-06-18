package io.axoniq.workshop.config;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Method to create bean that return swagerr docket.
	 * @return docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("io.axoniq.workshop")).paths(PathSelectors.any()).build()
				.apiInfo(metaData());
	}
	/**
	 * This method return the swager ApiInfo.
	 * @return apiInfo
	 */
	private ApiInfo metaData() {
		return new ApiInfoBuilder().title("Coding Task for Axoiq Workshop")
				.description("\"Swagger Configuration for applicaiion\"").version("1.1.0").license("Unlicensed")
				.build();
	}
}
