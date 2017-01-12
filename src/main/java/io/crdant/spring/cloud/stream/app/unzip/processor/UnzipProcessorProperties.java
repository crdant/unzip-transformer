package io.crdant.spring.cloud.stream.app.unzip.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("unzip")
public class UnzipProcessorProperties {

    String entry;

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

}
