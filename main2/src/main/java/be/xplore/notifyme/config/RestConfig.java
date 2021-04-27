package be.xplore.notifyme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure settings for rest communication.
 */
@Configuration
@EnableWebMvc
public class RestConfig {

  /**
   * Configures CORS policy.
   *
   * @return new WebMvcConfigurer with our settings.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*");
      }
    };
  }

  /**
   * Configures a default rest template.
   *
   * @return rest template with reusable settings.
   */
  @Bean
  public RestTemplate restTemplate() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(3000);
    factory.setReadTimeout(3000);
    return new RestTemplate(factory);
  }

  /**
   * Configures a default header for json requests.
   *
   * @return header that sets content type to application/json
   */
  @Bean(name = "jsonRequest")
  public HttpHeaders httpHeadersJson() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  /**
   * Configures a default header for encoded form requests.
   *
   * @return header that sets content type to application_form_urlencoded
   */
  @Bean(name = "xformRequest")
  public HttpHeaders httpHeadersXform() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }
}
