package app; 

import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; 

@SpringBootApplication
public class BakerApplication { 

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(BakerApplication.class, args);
    }

}