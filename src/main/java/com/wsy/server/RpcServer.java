package com.wsy.server;

import lombok.Data;

/**
 * @author wangshuangyong 2021.3.22
 */

@Data
public abstract class RpcServer {

    private int port;

    private String protocol;

    private RequestHandler requestHandler;

    public abstract void start();

    public abstract void stop();

    public RpcServer(int port, String protocol, RequestHandler requestHandler) {
        super();
        this.port = port;
        this.protocol = protocol;
        this.requestHandler = requestHandler;
    }

}
