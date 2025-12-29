package app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")         // All paths
                .allowedOrigins("*")        // Allow all origins
                .allowedMethods("*")        // Allow all HTTP methods
                .allowedHeaders("*")        // Allow all headers
                .allowCredentials(false)    // Credentials with '*' are not allowed by spec
                .maxAge(3600);              // Cache preflight for 1 hour
    }
}
