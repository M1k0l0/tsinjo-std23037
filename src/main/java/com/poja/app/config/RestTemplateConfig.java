package com.poja.app.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Value("${vola.api-key:fake}")
  private String volaApiKey;

  @Value("${vola.connect-timeout-ms}")
  private int connectTimeoutMs;

  @Value("${vola.read-timeout-ms}")
  private int readTimeoutMs;

  @Bean
  public RestTemplate volaRestTemplate(RestTemplateBuilder builder) {
    ClientHttpRequestInterceptor authInterceptor =
        (request, body, execution) -> {
          request.getHeaders().setBearerAuth(volaApiKey);
          request.getHeaders().set("Accept", "application/json");
          return execution.execute(request, body);
        };

    return builder
        .rootUri(
            "${vola.base-url}") // workaround: we will still use absolute URIs in code if preferred
        .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
        .setReadTimeout(Duration.ofMillis(readTimeoutMs))
        .additionalInterceptors(authInterceptor)
        .build();
  }
}
