package io.crdant.spring.cloud.stream.app.unzip.processor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;

@EnableBinding(Processor.class)
@EnableConfigurationProperties(UnzipProcessorProperties.class)
public class UnzipProcessorConfiguration {
}
