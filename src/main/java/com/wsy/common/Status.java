package com.wsy.common;

/**
 * @author wangshuangyong 2021.3.22
 */

public enum Status {

    SUCCESS(200, "SUCCESS"), NOT_FOUND(404, "NOT FOUND"), ERROR(500, "ERROR");

    private int code;

    private String message;

    private Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMEssage() {
        return message;
    }

}
