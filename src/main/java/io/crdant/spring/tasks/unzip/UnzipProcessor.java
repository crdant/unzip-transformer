package io.crdant.spring.tasks.unzip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extract a file from the zip file provided in the payload.
 */
@EnableBinding(Processor.class)
@ConfigurationProperties("module.unzip")
@EnableConfigurationProperties(UnzipProcessorProperties.class)
@MessageEndpoint
public class UnzipProcessor {
    int BUFFER_SIZE = 8*1024;
    private static Logger logger = LoggerFactory.getLogger(UnzipProcessor.class);

    @Autowired
    private UnzipProcessorProperties properties;

    @ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Object transform(Object payload) {
        try {
            byte[] outputPayload = null;
            if ( !isZip(payload) ) return null ;
            
            if ( hasSingleEntry(payload) ) {
                outputPayload = extractSingleEntry(payload);
            } else {
                outputPayload = extractEntry(payload, properties.getFile());
            }
            return outputPayload ;
        } catch ( IOException ioEx ) {
            logger.error("Error reading the zip file");
            return null ;
        }
    }

    private boolean isZip(Object payload) {
        try {
            ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream((byte[]) payload));
            return (zip.getNextEntry() != null);
        } catch ( IOException ioEx ) {
            ioEx.printStackTrace();
            return false ;
        }
    }

    private boolean hasSingleEntry(Object payload) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream((byte[]) payload));
        return ( zip.getNextEntry() != null ) && ( zip.getNextEntry() == null );
    }

    protected byte[] extractSingleEntry(Object payload) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream((byte[]) payload));
        ZipEntry entry = zip.getNextEntry();
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] result = null;

        logger.debug("extracting single file: " + entry.getName());
        while ((bytesRead = zip.read(buffer, 0, BUFFER_SIZE)) != -1) {
            content.write(buffer, 0, bytesRead);
        }
        return content.toByteArray();
    }

    protected byte[] extractEntry(Object payload, String name) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream((byte[]) payload));
        ZipEntry entry ;
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] result = null;

        while ( ( entry = zip.getNextEntry() ) != null ) {
            if ( entry.getName().equals(name) ) {
                logger.debug("extracting file: " + name);
                while ( ( bytesRead = zip.read(buffer, 0, BUFFER_SIZE) ) != -1 ) {
                    content.write(buffer, 0, bytesRead);
                }
                result = content.toByteArray();
            }
        }
        return result;
    }
}
