package br.com.poc;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * Essa implementação de correlation id serve para exemplificar
 * o funcionamento de filtros do Zuul, correlation ids reais devem
 * ser criados usando a dependência Spring Sleuth.
 * 
 * Para utilizar Spring Sleuth com as configurações padrões basta
 * incluir a dependência ao pom, esse projeto tem essa dependência
 * para mostrar o funcionamento do correlation id implementado manualmente
 * e o do Spring Sleuth, que é colocado no log em toda requisição HTTP. 
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Arrays.asList(new OutRestTemplateInterceptor()));
		return restTemplate;
	}
}
