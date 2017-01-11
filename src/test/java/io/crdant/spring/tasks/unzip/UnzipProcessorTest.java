package io.crdant.spring.tasks.unzip;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UnzipProcessorApplication.class})
@DirtiesContext
public abstract class UnzipProcessorTest {

    @Autowired
    protected Processor channels ;

    @Autowired
    protected MessageCollector messageCollector;


    @SpringBootTest
    public static class TestExtractsSingleEntry extends UnzipProcessorTest {
        @Test
        public void testRequest() throws Exception {
            byte[] content = new String("Test content").getBytes();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            ZipEntry entry = new ZipEntry("test.txt");
            ZipOutputStream zip = new ZipOutputStream(output);
            zip.putNextEntry(entry);
            zip.write(content, 0, content.length);
            zip.close();

            channels.input().send(new GenericMessage<Object>(output.toByteArray()));
            assertThat(messageCollector.forChannel(channels.output()), receivesPayloadThat(is(content)));
        }
    }

    @SpringBootTest("file=second.txt")
    public static class TestSelectsFile extends UnzipProcessorTest {
        @Test
        public void testRequest() throws Exception {
            byte[] content1 = new String("First file").getBytes();
            byte[] content2 = new String("Second file").getBytes();

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            ZipEntry entry1 = new ZipEntry("first.txt");
            ZipEntry entry2 = new ZipEntry("second.txt");
            ZipOutputStream zip = new ZipOutputStream(output);
            zip.putNextEntry(entry1);
            zip.write(content1, 0, content1.length);
            zip.putNextEntry(entry2);
            zip.write(content2, 0, content2.length);
            zip.close();

            channels.input().send(new GenericMessage<Object>(output.toByteArray()));
            assertThat(messageCollector.forChannel(channels.output()), receivesPayloadThat(is(content2)));
        }
    }

    @SpringBootTest("file=bytes")
    public static class handlesBinary extends UnzipProcessorTest {
        @Test
        public void testRequest() throws Exception {
            byte[] content = new byte[100];
            new Random().nextBytes(content);

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            ZipEntry entry1 = new ZipEntry("bytes");
            ZipOutputStream zip = new ZipOutputStream(output);
            zip.putNextEntry(entry1);
            zip.write(content, 0, content.length);
            zip.close();

            channels.input().send(new GenericMessage<Object>(output.toByteArray()));
            assertThat(messageCollector.forChannel(channels.output()), receivesPayloadThat(is(content)));
        }
    }

    @SpringBootTest
    public static class rejectsNotZip extends UnzipProcessorTest {

        @Test
        public void testRequest() throws Exception {
            byte[] content = new byte[100];

            channels.input().send(new GenericMessage<Object>(content));
            assertThat(messageCollector.forChannel(channels.output()).poll(10, MILLISECONDS), is(nullValue(Message.class)));
        }
    }


}
