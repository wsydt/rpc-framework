package com.wsy.server.register;

import com.alibaba.fastjson.JSON;
import com.wsy.discovery.ServiceInfo;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

/**
 * @author wangshuangyong 2021.3.26
 */

public class ZooKeeperServiceRegister extends DefaultServiceRegister {

    private ZooKeeper zooKeeper;

    private static final String centerRootPath = "/rpc-framework";

    private static final String zooKeeperUrl = "localhost";

    private static final int port = 2181;

    private CountDownLatch countDownLatch;

    public ZooKeeperServiceRegister() throws IOException, InterruptedException, KeeperException {
        this(zooKeeperUrl + ":" + port);
    }

    public ZooKeeperServiceRegister(String address) throws IOException, InterruptedException, KeeperException {
        countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(address, Integer.MAX_VALUE, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                if (event.getType() == Watcher.Event.EventType.None) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        Stat exists = zooKeeper.exists(centerRootPath, true);
        if (exists == null) {
            zooKeeper.create(centerRootPath, centerRootPath.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    @Override
    public void register(ServiceObject serviceObject, int port, String protocol) throws UnknownHostException, KeeperException, InterruptedException {
        super.register(serviceObject, port, protocol);
        ServiceInfo serviceInfo = new ServiceInfo();
        String host = InetAddress.getLocalHost().getHostAddress();
        serviceInfo.setAddress(host + ":" + port);
        serviceInfo.setName(serviceObject.getName());
        serviceInfo.setProtocol(protocol);
        exportService(serviceInfo);
    }

    private void exportService(ServiceInfo serviceInfo) throws KeeperException, InterruptedException {
        String serviceName = serviceInfo.getName();
        String content = JSON.toJSONString(serviceInfo);
        try {
            content = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = centerRootPath + "/" + serviceName;
        Stat exists = zooKeeper.exists(servicePath, true);
        if (exists == null) {
            zooKeeper.create(servicePath, serviceName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String contentPath = servicePath + "/service";
        zooKeeper.create(contentPath, content.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

}
