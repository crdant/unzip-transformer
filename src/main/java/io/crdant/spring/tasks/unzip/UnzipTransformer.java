package io.crdant.spring.tasks.unzip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Created by crdant on 1/9/17.
 */
@EnableBinding(Processor.class)
@ConfigurationProperties("module.unzip")
public class UnzipTransformer {
    private static Logger logger = LoggerFactory.getLogger(UnzipTransformer.class);

    @ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Object transform(Object payload) {
        return payload;
    }
}
