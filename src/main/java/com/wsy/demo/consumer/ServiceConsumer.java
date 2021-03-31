package com.wsy.demo.consumer;

import com.wsy.client.ClientStubProxyFactory;
import com.wsy.demo.HelloService;

import java.io.IOException;
import java.util.List;

/**
 * @author wangshuangyong 2021.3.30
 */

public class ServiceConsumer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ClientStubProxyFactory proxy = new ClientStubProxyFactory();
        HelloService helloService = proxy.getProxy(HelloService.class);
        List<String> tag = helloService.getTag();
        System.out.println(tag);
    }

}
