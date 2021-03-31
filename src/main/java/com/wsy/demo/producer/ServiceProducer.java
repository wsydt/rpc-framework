package com.wsy.demo.producer;

import com.wsy.demo.HelloService;
import com.wsy.server.NettyRpcServer;
import com.wsy.server.register.ServiceObject;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author wangshuangyong 2021.3.30
 */

public class ServiceProducer {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        HelloService hello = new HelloServiceImpl();
        ServiceObject serviceObject = new ServiceObject(HelloService.class.getSimpleName(), HelloService.class, hello);
        NettyRpcServer server = new NettyRpcServer();
        server.getRequestHandler().getServiceRegister().register(serviceObject, server.getPort(),server.getRequestHandler().getMessageProtocol().getProtocolName());
        server.start();
        System.in.read();
        server.stop();
    }

}
