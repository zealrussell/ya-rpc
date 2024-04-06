package com.zeal.rpc.server.core;

import com.zeal.rpc.common.protocol.RpcRequest;
import java.util.Map;

import com.zeal.rpc.common.protocol.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

/**
 * WHAT THE ZZZZEAL
 * netty的handler
 * @Author zeal
 * @Date 2022/10/5 13:22
 * @Version 1.0
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 构造response
     * @param channelHandlerContext nettychannel
     * @param request 请求
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handler(request);
            response.setResult(result);
        } catch (Throwable throwable) {
            // 失败时填入异常
            response.setError(throwable);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 异常处理
     * @param channelHandlerContext
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        LOGGER.error("something wrong on the server" , cause);
        channelHandlerContext.close();
    }

    /**
     * 基于spring反射执行方法
     * @param request 请求
     * @return 执行方法
     * @throws Throwable 异常
     */
    private Object handler(RpcRequest request) throws Throwable {
        // 通过classname构建class
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        Class<?> serviceClass = serviceBean.getClass();

        // 获取 方法名、参数类型、参数值
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
        
        return fastMethod.invoke(serviceBean, parameters);
    }
}
