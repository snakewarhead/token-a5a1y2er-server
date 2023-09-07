package com.cq.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HtmlContentBuilder {
    private final TemplateEngine templateEngine;

    public String table(String title, List<String> headers, List<List<String>> contents) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("headers", headers);
        context.setVariable("contents", contents);
        return templateEngine.process("table", context);
    }
}
