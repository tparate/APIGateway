package net.tuparate.gateway;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@Log4j2
@EnableZuulProxy
@SpringBootApplication
public class APIGatewayZuulProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(APIGatewayZuulProxyApplication.class, args);
		log.info("APIGateway Application is started.....");
	}

}
