package in.lakshay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "in.lakshay")
public class MovieReviewSystemApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MovieReviewSystemApiApplication.class, args);
	}
}