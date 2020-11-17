package com.thkmon.wgbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
public class WebGameBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebGameBaseApplication.class, args);
	}
	
	
	// websocket
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}