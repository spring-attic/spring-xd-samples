package io.pivotal.demo.smartgrid.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Thomas Darimont
 */
@ComponentScan
@EnableAutoConfiguration
public class SmartGridFrontend {

    public static void main(String[] args) {
        SpringApplication.run(SmartGridFrontend.class, args);
    }
}
