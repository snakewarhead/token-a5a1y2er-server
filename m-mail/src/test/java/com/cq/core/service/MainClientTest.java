package com.cq.core.service;

import com.cq.core.vo.MailMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainClientTest {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testSend() throws IOException {
        MailMsg m = MailMsg.builder()
                .subject("test")
                .text("aaabbb")
                .silent(MailMsg.Silent.builder()
                        .hash("1111")
                        .deadline(60000L)
                        .build())
                .build();
        mailClient.send(m);
    }

    @Test
    public void testSendTextMe() throws IOException {
        mailClient.sendTextMe("test", "error");
    }
}
