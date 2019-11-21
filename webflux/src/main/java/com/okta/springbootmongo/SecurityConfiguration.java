package com.okta.springbootmongo;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;

//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  // @Bean
  // public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
  //   http
  //       .authorizeExchange()
  //       .pathMatchers(HttpMethod.POST, "/kayaks/**").hasAuthority("Admin")
  //       .anyExchange().authenticated()
  //       .and()
  //       .oauth2ResourceServer()
  //       .jwt();
  //   return http.build();
  // }

}

