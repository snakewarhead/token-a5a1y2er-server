package com.cq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JSONResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private int code;

    private String message;

    private T result;

}
