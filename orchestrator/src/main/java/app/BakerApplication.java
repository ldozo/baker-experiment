package app; 

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ing.baker.runtime.inmemory.InMemoryBaker;
import com.ing.baker.runtime.javadsl.Baker;
import app.interactions.impl.OpenAccountImpl;
import app.interactions.impl.RegisterCustomerImpl;
import app.interactions.impl.SendEmailImpl; 

@SpringBootApplication
public class BakerApplication { 

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(BakerApplication.class, args);
    }

    @Bean
    public Baker baker() {
        var baker = InMemoryBaker.java(
            List.of(new RegisterCustomerImpl(), new OpenAccountImpl(), new SendEmailImpl())
        );

        return baker;      
    } 
}