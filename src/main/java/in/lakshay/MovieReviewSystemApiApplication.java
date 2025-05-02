package in.lakshay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// main app entry point - starts the spring boot app
@SpringBootApplication
@ComponentScan(basePackages = "in.lakshay") // scan our pkg for components
public class MovieReviewSystemApiApplication {
	// main method - this is where everything begins
	public static void main(String[] args) {
		// fire up the app!
		SpringApplication.run(MovieReviewSystemApiApplication.class, args);
	}
}