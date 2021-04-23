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

@Configuration
@EnableWebMvc
public class RestConfig {

 @Bean
 public WebMvcConfigurer corsConfigurer(){
   return new WebMvcConfigurer() {
     @Override
     public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/**").allowedOriginPatterns("*");
     }
   };
 }

  @Bean
  public RestTemplate restTemplate() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(3000);
    factory.setReadTimeout(3000);
    return new RestTemplate(factory);
  }

  @Bean(name = "jsonRequest")
  public HttpHeaders httpHeadersJson() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @Bean(name = "xformRequest")
  public HttpHeaders httpHeadersXForm() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }
}
