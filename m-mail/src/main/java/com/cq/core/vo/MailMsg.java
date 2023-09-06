package com.cq.core.vo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailMsg {

    private String subject;
    private String text;

    public String toJson() {
        JSONObject obj = JSONUtil.createObj();
        obj.put("subject", subject);
        obj.put("text", text);
        return obj.toJSONString(0);
    }

    /**
     * format: xxx@xxx.com,yyy@yyy.com
     */
    private String emails;
    private Silent silent;

    @Data
    @Builder
    public static class Silent {
        private String hash;
        private Long deadline;

        public String toJson() {
            JSONObject obj = JSONUtil.createObj();
            obj.put("hash", hash);
            obj.put("deadline", deadline);
            return obj.toJSONString(0);
        }
    }
}
