package com.wsy.discovery;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * @author wangshuangyong 2021.3.25
 */

public class ZooKeeperServiceInfoDiscovery implements ServiceInfoDiscovery {

    private ZooKeeper zooKeeper;

    private static final String defaultAddress = "localhost";

    private static final int defaultPort = 2181;

    private String centerRootPath = "/rpc-framework";

    public ZooKeeperServiceInfoDiscovery() throws IOException {
        this(defaultAddress, defaultPort);
    }

    public ZooKeeperServiceInfoDiscovery(String address, int port) throws IOException {
        zooKeeper = new ZooKeeper(address + ":" + port, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
    }

    @Override
    public List<ServiceInfo> getServiceInfo(String name) throws KeeperException, InterruptedException {
        String servicePath = centerRootPath + "/" + name + "/service";
        List<String> serviceList = zooKeeper.getChildren(servicePath, false);
        for (String service : serviceList) {

        }
        return null;
    }
}
