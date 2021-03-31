package com.wsy.server.register;

import org.apache.zookeeper.KeeperException;

import java.net.UnknownHostException;

public interface ServiceRegister {

    void register(ServiceObject serviceObject, int port, String protocol) throws UnknownHostException, KeeperException, InterruptedException;

    ServiceObject getServiceObject(String name);


}
