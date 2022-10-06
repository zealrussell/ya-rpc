package com.zeal.server.core;

import com.zeal.common.Constant;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 12:04
 * @Version 1.0
 */
public class Registry {
    private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);
    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public Registry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    /**
     * 注册到zookeeper，创建节点
     * @param data
     */
    public void registry(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                createNode(zk, data);
            }
        }
    }

    /**
     * 连接zookeeper服务器，当SyncConnected时继续
     * @return zk
     */
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Can't get connection for zookeeper,registryAddress:{}", registryAddress, e);
        }
        return zk;
    }

    /**
     * 创建节点,值为data
     * @param zk zookeeper对象
     * @param data 数据
     */
    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

}
