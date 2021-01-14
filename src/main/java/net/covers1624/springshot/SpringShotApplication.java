package net.covers1624.springshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SpringShotApplication {

    /**
     * Designates special URLS that uploaded data should not use as its name
     */
    public static final Set<String> SPECIAL_URLS = new HashSet<>(Arrays.asList("panel", "api"));

    public static void main(String[] args) {
        SpringApplication.run(SpringShotApplication.class, args);
    }

    @Bean
    @Autowired
    public CommonsMultipartResolver multipartResolver(SpringShotProperties properties) {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(properties.getMaxUploadSize());
        return resolver;
    }

    @Autowired
    public void validateProperties(SpringShotProperties properties) {
        if (properties.getObjectsDir() == null) {
            throw new RuntimeException("Objects dir not set.");
        }
    }

}
