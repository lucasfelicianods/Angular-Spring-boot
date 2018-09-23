package com.example.algamoney.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // filtro que tenha um prioridade muita alta
public class RefreshTokenCookiePreProcessorFilter implements Filter{

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request; // pergar a requisição do http
		
		if("/oauth/token".equalsIgnoreCase(req.getRequestURI())  // enviar o refreh_token junto com os parametros da requisicao
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null){
			for (Cookie cookie : req.getCookies()){
				if(cookie.getName().equals("refreshToken")){
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken); // substituindo com a requisicao juntamente com o refreshToken
				}
			}
					
		}
		chain.doFilter(req, response);
	}

	
	
	@Override
	public void destroy() {
		
	}

	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	static class MyServletRequestWrapper extends HttpServletRequestWrapper {
		
		private String refreshToken;

		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			
			this.refreshToken = refreshToken;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() { // pegar os parametros da requisicao
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap()); // os valores do mapa da requisicao continua
			map.put("refresh_token", new String[] {refreshToken}); // nome que ele vai usar para recuperar o refreshToken
			map.setLocked(true); // travar o map da requisicao
			return map;
			
		}
		
		
	}

}
