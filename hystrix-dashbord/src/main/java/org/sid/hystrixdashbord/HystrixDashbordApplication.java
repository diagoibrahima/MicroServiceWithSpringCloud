package org.sid.hystrixdashbord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashbordApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixDashbordApplication.class, args);
	}

}
