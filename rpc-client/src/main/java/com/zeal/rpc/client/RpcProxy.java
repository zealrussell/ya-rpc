package com.zeal.rpc.client;

import com.zeal.rpc.common.protocol.RpcRequest;
import com.zeal.rpc.common.protocol.RpcResponse;
import org.springframework.cglib.proxy.Proxy;


import java.util.UUID;

/**
 * WHAT THE ZZZZEAL
 * rpc代理
 * @Author zeal
 * @Date 2022/10/5 21:56
 * @Version 1.0
 */
public class RpcProxy {
    private String serverAddress;
    
    private Discovery discovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(Discovery discovery) {
        this.discovery = discovery;
    }

    public RpcProxy(String serverAddress, Discovery discovery) {
        this.serverAddress = serverAddress;
        this.discovery = discovery;
    }
    
    public <T> T createService(Class<?> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass},
                (proxy, method, args) -> {
                    // 构造rpc请求
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    
                    // 获取 服务器地址
                    if (discovery != null) {
                        serverAddress = discovery.discover();
                    }
                    String[] array = serverAddress.split(":");
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);

                    RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
                    RpcResponse response = client.send(request);  // 通过 RPC客户端发送RPC请求并获取RPC响应
                    if (response.isError()) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                });
    }
}
