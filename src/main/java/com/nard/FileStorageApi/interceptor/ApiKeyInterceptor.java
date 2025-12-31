package com.nard.FileStorageApi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.nard.FileStorageApi.model.User;
import com.nard.FileStorageApi.service.ApiKeyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

  @Autowired
  private ApiKeyService apiKeyService;

  @Override
  public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request,
      @org.springframework.lang.NonNull HttpServletResponse response,
      @org.springframework.lang.NonNull Object handler)
      throws Exception {

    // Skip authentication for error endpoints
    String path = request.getRequestURI();
    if (path.startsWith("/error")) {
      return true;
    }

    // Extract API key from Authorization header
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("ApiKey ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter()
          .write("{\"error\": \"Missing or invalid Authorization header. Expected: Authorization: ApiKey <key>\"}");
      return false;
    }

    String apiKey = authHeader.substring(7); // Remove "ApiKey " prefix

    // Validate API key
    var userOpt = apiKeyService.validateApiKey(apiKey);
    if (userOpt.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Invalid API key\"}");
      return false;
    }

    // Store user in request attribute for controllers to access
    User user = userOpt.get();
    request.setAttribute("authenticatedUser", user);

    return true;
  }

}
