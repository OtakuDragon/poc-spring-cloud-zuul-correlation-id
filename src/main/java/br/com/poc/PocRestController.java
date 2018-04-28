package br.com.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * Neste exemplo:
 * 
 * 1. Chega uma requisição externa ao Zuul gateway direcionada ao método service1
 * 2. O Zuul percebe que é uma requsição externa(nova) porque o header correlation-id
 *    não existe, então ele cria um novo e seta no header, o InServletFilter seta esse
 *    correlation-id definido pelo Zuul em uma variavel ThreadLocal para que ela esteja
 *    acessivel.
 * 3. O service1 printa o correlation id que foi colocado na variavel ThreadLocal para mostrar
 *    que é o mesmo que chegou do Zuul Gateway, e então chama o service2 apartir do Zuul Gateway
 * 4. O OutRestTemplateInterceptor intercepta a requisição e adiciona o header correlation-id a requisição
 *    com o valor da variavel ThreadLocal, e manda a requisição.
 * 5. A requisição chega ao Zuul Gateway que percebe que o correlation-id está preenchido portanto identifica
 *    que não deve criar um novo apenas propaga-lo e manda a requisição para o service2.
 * 6. A requisição chega ao InServletFilter que seta o correlation-id que chegou do Zuul que no caso é o mesmo
 *    valor na variavel ThreadLocal.
 * 7. O service 2 printa o mesmo correlation-id do service1 e a requisição acaba.
 * 
 * OBSERVAÇÃO: Nesse caso os dois serviços que fazem parte de uma mesma requisição estão no mesmo microservice
 *             por simplicidade, se service1 e service2 estivessem em dois microservices diferentes o funcionamento
 *             seria o mesmo desde que eles utilizassem a mesma logica das classes InServletFilter e OutRestTemplateInterceptor
 *             e o Zuul Gateway se comportasse da mesma forma.
 * OBSERVAÇÃO: Esta POC utiliza o poc-spring-cloud-eureka-server para comunicação serviço-zuul-serviço.
 * ATENÇÃO: Caso a requsição do service1 ao service2 fosse através de um @HystrixCommand, o correlation id guardado em
 * 			ThreadLocal não estaria disponivel ao service2 porque o Hystrix por padrão cria uma nova Thread para a execução
 * 			do @HystrixCommand, existem maneiras de contornar isso mas elas não estão contidas neste exemplo.
 */
@RestController
public class PocRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(method=RequestMethod.GET, value = "/service1")
	public void service1() {
		logger.info("Service 1: "+ ThreadLocalData.correlationId.get());
		restTemplate.getForEntity("http://poc-spring-cloud-zuul/api/correlationid/service2", String.class);
	}

	
	@RequestMapping(method=RequestMethod.GET, value = "/service2")
	public void service2() {
		logger.info("Service 2: "+ ThreadLocalData.correlationId.get());
	}

}
