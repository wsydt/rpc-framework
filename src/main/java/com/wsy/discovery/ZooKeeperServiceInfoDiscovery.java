package com.wsy.discovery;

import com.alibaba.fastjson.JSONObject;
import com.wsy.server.register.ServiceObject;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author wangshuangyong 2021.3.25
 */

public class ZooKeeperServiceInfoDiscovery implements ServiceInfoDiscovery {

    private ZooKeeper zooKeeper;

    private static final String defaultAddress = "localhost";

    private static final int defaultPort = 2181;

    private static final String centerRootPath = "/rpc-framework";

    public ZooKeeperServiceInfoDiscovery() throws IOException, InterruptedException {
        this(defaultAddress, defaultPort);
    }

    public ZooKeeperServiceInfoDiscovery(String address, int port) throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(address + ":" + port, Integer.MAX_VALUE, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                if (event.getType() == Watcher.Event.EventType.None) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }

    @Override
    public List<ServiceInfo> getServiceInfo(String name) throws KeeperException, InterruptedException {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        String servicePath = centerRootPath + "/" + name;
        List<String> serviceList = zooKeeper.getChildren(servicePath, false);
        for (String service : serviceList) {
            try {
                byte[] data = zooKeeper.getData(servicePath + "/" + service, true, null);
                String decode = URLDecoder.decode(new String(data), "UTF-8");
                ServiceInfo serviceInfo = JSONObject.parseObject(decode, ServiceInfo.class);
                serviceInfoList.add(serviceInfo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return serviceInfoList;
    }
}
