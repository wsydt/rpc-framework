package com.wsy.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangshuangyong 2021.3.22
 */

@Data
public class Response implements Serializable {

    private Status status;

    private Map<String, String> header = new HashMap<>();

    private Object returnValue;

    private Exception exception;

    public Response(Status status) {
        this.status = status;
    }

}
