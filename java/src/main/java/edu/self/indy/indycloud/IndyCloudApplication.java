package edu.self.indy.indycloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndyCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndyCloudApplication.class, args);
	}
}


// import org.springframework.boot.builder.SpringApplicationBuilder;
// import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//
// @SpringBootApplication
// public class IndyCloudApplication extends SpringBootServletInitializer
// {
//     public static void main(String[] args)
//     {
//         new IndyCloudApplication().configure(new SpringApplicationBuilder(IndyCloudApplication.class)).run(args);
//     }
// }