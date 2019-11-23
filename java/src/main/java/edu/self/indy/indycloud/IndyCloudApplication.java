package edu.self.indy.indycloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class IndyCloudApplication {

	public static void main(String[] args) {
		System.out.println("java.library.path: " + System.getProperty("java.library.path"));
		SpringApplication application = new SpringApplication(IndyCloudApplication.class);
		//SpringApplication.run(IndyCloudApplication.class, args);
		//addInitHooks(application);
		application.run(args);
	}

//	static void addInitHooks(SpringApplication application) {
//		application.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
//			//String version = event.getEnvironment().getProperty("java.runtime.version");
//			//log.info("Running with Java {}", version);
//			//System.out.println("Now create the trustee wallet and get the walletId and trusteeDID!!");
//		});
//	}
}
