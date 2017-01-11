package io.crdant.spring.tasks.unzip;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class UnzipProcessorProperties {

    String file ;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
