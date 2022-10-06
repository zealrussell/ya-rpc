package com.zeal.common.protocol;

import lombok.Data;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/4 23:02
 * @Version 1.0
 */
@Data
public class RpcResponse {
    private String requestId;

    private Throwable error;

    private Object result;

}
