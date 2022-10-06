package com.zeal.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;


/**
 * WHAT THE ZZZZEAL
 * Rpc服务类
 * @Author zeal
 * @Date 2022/10/5 12:01
 * @Version 1.0
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    /**
     * 服务器地址，通过spring注册
     */
    private String serverAddress;

    private Registry registry;

    /**
     * zookeeper键值对
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcServer(String serverAddress, Registry registry) {
        this.serverAddress = serverAddress;
        this.registry = registry;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
