package com.wandercosta.annotation.example;

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private PersonConfig personConfig;

    public Application(PersonConfig personConfig) {
        this.personConfig = personConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return personConfig.toString();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> logBeans(ctx.getBeanDefinitionNames());
    }

    private void logBeans(String[] beans) {
        String log = Stream.of(beans).sorted()
            .map(bean -> " - " + bean)
            .reduce((s1, s2) -> s1.concat("\n").concat(s2))
            .orElse("No beans found! What?");
        LOG.info("Let's inspect the beans provided by Spring Boot:\n{}", log);
    }

}