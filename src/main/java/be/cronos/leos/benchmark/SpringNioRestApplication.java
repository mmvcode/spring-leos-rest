package be.cronos.leos.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.rx.RxJavaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import be.cronos.leos.benchmark.filter.CorsFilter;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCircuitBreaker
@Import(RxJavaAutoConfiguration.class)
@SpringBootApplication
@EnableSwagger2
public class SpringNioRestApplication {

    @Bean
    public Docket nioApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //.groupName("full-spring-nio-api")
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> registerCorsFilter() {
        CorsFilter cf = new CorsFilter();
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(cf);
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadGroupName("MyThreadGroup");
        executor.setThreadNamePrefix("MyThreadNamePrefix");
        executor.setCorePoolSize(5000);
        return executor;
    }

    @Bean
    public ServletRegistrationBean<HystrixMetricsStreamServlet> hystrixMetricsStreamServlet() {
        return new ServletRegistrationBean<>(new HystrixMetricsStreamServlet(), "/hystrix.stream");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot LEOS Rest API")
                .description("A couple of services to test Java NIO Performance")
                .termsOfServiceUrl("https://www.eupl.eu/1.2/en")
                .license("EUPL v1.2")
                .licenseUrl("https://www.eupl.eu/1.2/en")
                .version("2.0")
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringNioRestApplication.class, args);
    }
}
