package br.com.poc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * 
 * Rest template interceptor que intercepta todas as
 * requisições http a outros serviços que esse serviço faz
 * e antes de executar a chamada seta no header o mesmo correlation-id
 * da requisição original que chegou no InServletFilter.
 * 
 * O Zuul gateway vai ver que nesta requisição o correlation-id
 * já está preenchido e não vai setar um novo, apenas propaga-lo
 * para o serviço de destino, que no caso dessa poc está nesse mesmo
 * micro serviço mas poderia ser em outro.
 *
 */
public class OutRestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

		logger.info("Setando correlation id na requisição saindo desse serviço e indo para o zuul: "+ ThreadLocalData.correlationId.get());
		
		httpRequest.getHeaders().add("correlation-id", ThreadLocalData.correlationId.get());
		
		return clientHttpRequestExecution.execute(httpRequest, body);
	}

}
