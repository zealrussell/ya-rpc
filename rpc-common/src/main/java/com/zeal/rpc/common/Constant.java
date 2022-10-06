package com.zeal.common;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 10:34
 * @Version 1.0
 */
public interface Constant {

    int ZK_SESSION_TIMEOUT = 5000;
    /**
     * zookeeper根节点
     */
    String ZK_REGISTRY_PATH = "/registry";
    /**
     * 服务数据存储ZK根节点
     */
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
