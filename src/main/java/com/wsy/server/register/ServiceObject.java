package com.wsy.server.register;

import lombok.Data;

/**
 * @author wangshuangyong 2021.3.22
 */
@Data
public class ServiceObject {

    private String name;

    private Class<?> inter;

    private Object object;

    public ServiceObject(String name, Class<?> inter, Object object){
        this.name = name;
        this.inter = inter;
        this.object = object;
    }

}
