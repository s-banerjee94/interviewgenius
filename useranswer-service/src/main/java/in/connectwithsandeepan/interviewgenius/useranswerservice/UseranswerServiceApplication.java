package in.connectwithsandeepan.interviewgenius.useranswerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UseranswerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UseranswerServiceApplication.class, args);
	}

}
