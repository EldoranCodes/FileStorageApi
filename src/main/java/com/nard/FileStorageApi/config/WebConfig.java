package com.nard.FileStorageApi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nard.FileStorageApi.interceptor.ApiKeyInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private ApiKeyInterceptor apiKeyInterceptor;

  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(apiKeyInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/error", "/actuator/**");
  }

}

