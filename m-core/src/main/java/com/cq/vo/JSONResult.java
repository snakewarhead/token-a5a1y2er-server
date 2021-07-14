package com.cq.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
public class JSONResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private int code;

    private String message;

    private T result;

    public static JSONResult error(int code, String msg) {
        return new JSONResult<>(false, code, msg, null);
    }

    public static JSONResult success(String msg) {
        return success(msg, null);
    }

    public static JSONResult success(String msg, Object result) {
        return new JSONResult<>(true, 200, msg, result);
    }

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
