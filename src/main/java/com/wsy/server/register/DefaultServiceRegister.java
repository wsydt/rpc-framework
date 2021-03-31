package com.wsy.server.register;

import lombok.Data;
import org.apache.zookeeper.KeeperException;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangshuangyong 2021.3.26
 */

public class DefaultServiceRegister implements ServiceRegister {

    private Map<String, ServiceObject> serviceMap = new HashMap<>();

    @Override
    public void register(ServiceObject serviceObject, int port, String protocol) throws UnknownHostException, KeeperException, InterruptedException {
        if (serviceObject == null) {
            throw new IllegalArgumentException("参数不能为空 ! ");
        }
        serviceMap.put(serviceObject.getName(), serviceObject);
    }

    @Override
    public ServiceObject getServiceObject(String name) {
        return this.serviceMap.get(name);
    }
}
