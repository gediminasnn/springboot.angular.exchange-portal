package com.example.exchangeportal.config;

import java.net.http.HttpClient;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import com.example.exchangeportal.factory.AutowiringSpringBeanJobFactory;

@Configuration
public class AppConfig {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		schedulerFactoryBean.setJobFactory(jobFactory);

		return schedulerFactoryBean;
	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}
}
