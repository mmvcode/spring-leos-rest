package be.cronos.leos.benchmark.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@NoArgsConstructor
@Slf4j
public class CorsFilter implements Filter {

	@Value("${cors.allowed.origins}")
	private String corsAllowedOrigins;

	public void init(FilterConfig fConfig) {
		WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(fConfig.getServletContext());
		ac.getAutowireCapableBeanFactory().autowireBean(this);
	}
    
	@Override
	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest hRequest = (HttpServletRequest)request;
		HttpServletResponse hResponse = (HttpServletResponse)response;
		
		String remoteHost = hRequest.getHeader("Origin");
		log.debug("### rest request from remote host[{}]",remoteHost);

		if(remoteHost!=null && (corsAllowedOrigins.contains(remoteHost) || corsAllowedOrigins.equals("*"))){
			log.debug("### adding Access Control Headers for {} ###",remoteHost);
			hResponse.setHeader("Access-Control-Allow-Origin", remoteHost);
			//hResponse.setHeader("Access-Control-Allow-Credentials", "true");
			hResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,Accept");
		}
		chain.doFilter(request, response);
	}
	
}
