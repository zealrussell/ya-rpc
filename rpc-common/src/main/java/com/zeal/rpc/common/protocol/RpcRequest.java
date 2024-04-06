package com.zeal.rpc.common.protocol;

import lombok.Data;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/4 22:58
 * @Version 1.0
 */
@Data
public class RpcRequest {
    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

}
