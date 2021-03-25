package com.wsy.discovery;

import org.apache.zookeeper.KeeperException;

import java.util.List;

public interface ServiceInfoDiscovery {

    List<ServiceInfo> getServiceInfo(String name) throws KeeperException, InterruptedException;

}
