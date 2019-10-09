package se.sigma.boostapp.boost_app_java.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    // Make Swagger meta-data available via <baseURL>/v2/api-docs/
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    // Make Swagger UI available via <baseURL>/swagger-ui.html
    registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
  }
}