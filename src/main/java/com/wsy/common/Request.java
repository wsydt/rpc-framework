package com.wsy.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangshuangyong 2021.3.22
 */

@Data
public class Request implements Serializable {

    private String serviceName;

    private String method;

    private Map<String, String> header = new HashMap<>();

    private Class<?> [] parameterType;

    private Object[] parameter;

}
