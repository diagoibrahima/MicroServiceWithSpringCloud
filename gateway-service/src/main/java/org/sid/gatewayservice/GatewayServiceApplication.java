package org.sid.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableHystrix
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}


	/* Access au webservice REST Countries v1 de l'api rapidapi qui est un webservice gratuit et public il suffi juste
	de s'inscrire pour avoir un x-rapidapi-key valide. Le chemin qui permet d'acceder a ce service est localhost:8888/publicCountries/all
	*/
	@Bean
	RouteLocator staticRoutes(RouteLocatorBuilder builder){
		return builder.routes()
				.route(r->r
						.path("/publicCountries/**")
						.filters(f->f
								.addRequestHeader("x-rapidapi-host", "restcountries-v1.p.rapidapi.com")
								.addRequestHeader("x-rapidapi-key", "619084cd54msh239e4a8abc724dbp13b1a1jsn290648533c2d")
								.rewritePath("/publicCountries/(?<segment>.*)","/${segment}")
								.hystrix(h->h.setName("countries").setFallbackUri("forward:/defaultCountries"))
						)
						.uri("https://restcountries-v1.p.rapidapi.com").id("r1"))
				.route(r->r
						.path("/muslim/**")
						.filters(f->f
								.addRequestHeader("x-rapidapi-host", "muslimsalat.p.rapidapi.com")
								.addRequestHeader("x-rapidapi-key", "619084cd54msh239e4a8abc724dbp13b1a1jsn290648533c2d")
								.rewritePath("/muslim/(?<segment>.*)","/${segment}")
						)
						.uri("https://muslimsalat.p.rapidapi.com").id("r2"))
				.build();
	}

	@Bean
	DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp){
		return new DiscoveryClientRouteDefinitionLocator(rdc,dlp);
	}


}

// Controller pour tester le service breaker
@RestController
class CircuitBreakerRestController{
	@GetMapping("/defaultCountries")
	public Map<String, String> countries(){
		Map<String, String> data = new HashMap<>();
		data.put("message","Default Countries");
		data.put("countries", "Maroc, Algerie, Tunisie") ;
		return data;
	}
}
