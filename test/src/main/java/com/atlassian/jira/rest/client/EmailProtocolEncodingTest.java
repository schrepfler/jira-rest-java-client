package com.atlassian.jira.rest.client;

import com.atlassian.jira.rest.client.internal.async.AsynchronousEmailRestClient;
import org.junit.Test;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class EmailProtocolEncodingTest {

    @Test
    public void emailTextConstructionIsParsable() throws Exception {
        String dest = "fred@google.com";
        String body = "roses are red.";
        String subject = "flowers";
        String s = AsynchronousEmailRestClient.constructEmail(dest, subject, body);
        InputStream stream = new ByteArrayInputStream(s.getBytes(Charset.forName("US-ASCII")));
        javax.mail.Message m = new MimeMessage(Session.getDefaultInstance(new Properties()), stream);

        assertEquals(subject, m.getSubject());
        assertEquals(body, m.getContent());
        assertEquals(dest, m.getAllRecipients()[0].toString());
    }


}
