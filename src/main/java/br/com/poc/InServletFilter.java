package br.com.poc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * Filtro http que executa antes de todas as requisições http
 * feitas a esse serviço, ele extrai o correlation-id gerado pelo
 * Zuul de um header customizado definido pelo gateway Zuul e seta
 * em uma variavel ThreadLocal para que o correlation id fique
 * disponivel durante todo o processamendo da requsição.
 *
 */
@Component
public class InServletFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = HttpServletRequest.class.cast(servletRequest);
		String correlationId = httpRequest.getHeader("correlation-id");
		logger.info("Setando correlation id na requisição chegando do zuul: "+ correlationId);
		ThreadLocalData.correlationId.set(correlationId);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}
	
	@Override
	public void destroy() {
	}

}
