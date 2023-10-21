package com.cq.core.service;

import com.cq.core.vo.MailMsg;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HtmlContentBuilderTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private HtmlContentBuilder builder;

    @Test
    public void table() throws IOException {
        List<String> headers = new ArrayList<>();
        headers.add("valuea");
        headers.add("valueb");

        List<List<String>> contents = new ArrayList<>();
        contents.add(new ArrayList<>() {
            {
                add("<span style=\"color: red;\">540488.07</span>");
                add("bbbbbbb");
            }
        });

        String ct = builder.table("test1", headers, contents);
        log.info(ct);

        MailMsg m = MailMsg.builder()
                .subject("test")
                .text(ct)
                .build();
        mailClient.send(m);

    }
}
