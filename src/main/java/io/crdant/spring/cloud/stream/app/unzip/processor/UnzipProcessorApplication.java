package io.crdant.spring.cloud.stream.app.unzip.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;

@EnableBinding(Processor.class)
@SpringBootApplication
public class UnzipProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnzipProcessorApplication.class, args);
    }
}
