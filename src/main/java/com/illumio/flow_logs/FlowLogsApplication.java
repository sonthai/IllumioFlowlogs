package com.illumio.flow_logs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FlowLogsApplication {
	@Autowired
	private DataLookupLoader loader;

	public static void main(String[] args) {
		SpringApplication.run(FlowLogsApplication.class, args);
	}

	@Bean
	InitializingBean loadData() {
		return () -> loader.loadData();
	}

}
