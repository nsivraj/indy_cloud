package edu.self.indy.indycloud;

import com.evernym.sdk.vcx.LibVcx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class IndyCloudApplication {

	public static void main(String[] args) {
		System.out.println("java.library.path: " + System.getProperty("java.library.path"));
		LibVcx.initByLibraryName("indycloud");
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