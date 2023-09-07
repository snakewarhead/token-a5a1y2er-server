package com.cq.core.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HtmlContentBuilderTest {
    @Autowired
    private HtmlContentBuilder builder;

    @Test
    public void table() {
        List<String> headers = new ArrayList<>();
        headers.add("valuea");
        headers.add("valueb");

        List<List<String>> contents = new ArrayList<>();
        contents.add(new ArrayList<>() {
            {
                add("aaaaaaa");
                add("bbbbbbb");
            }
        });

        String ct = builder.table("test1", headers, contents);
        log.info(ct);
    }
}
