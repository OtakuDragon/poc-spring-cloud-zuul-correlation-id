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
 * Um trace do Spring Sleuth que é logado em cada mensagem de log impressa
 * no stdout apartid do momento em que a dependência do Spring Sleuth é adicionado
 * a aplicação Spring Boot é composta da seguinte forma:
 * 
 * [spring.application.name,trace-id,span-id, boolean]
 * 
 * spring.application.name = Nome do microserviço
 * trace-id = Identificador único da requsição completa
 * span-id = Identificador único de uma parte da requisição
 * boolean = Booleano que diz se a mensagem foi enviada ao zipkin ou não,
 * 			 não garante entrega, apenas diz que houve uma tentativa de enviar
 * 			 o trace, se o servidor zipkin estiver fora, não acontecem erros.
 * 
 * Zipkin é uma aplicação web que se integra com o Spring Sleuth para exibir
 * visualmente os traces capturados pelo Spring Sleuth, no pom e application.yml
 * desse projeto tem comentarios explicando como configurar a comunicação com
 * um servidor zipkin, ele loga traces do Sleuth de requisições HTTP, envio e
 * recebimento de mensagens JMS e Spans(conjuntos de traces) costomizados criados
 * manipulando o Spring Bean Trace que o Spring Sleuth oferece.
 * 
 * Na interface web do Zipkin é possivel consultar traces pelo trace-id.
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
