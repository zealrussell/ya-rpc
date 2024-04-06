package com.zeal.rpc;

import com.zeal.rpc.client.RpcProxy;
import com.zeal.rpc.server.service.CalculatorService;
import com.zeal.rpc.server.service.CharacterService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    
    
    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
        RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
        
        // 服务一
        CalculatorService calculatorService = rpcProxy.createService(CalculatorService.class);
        float result1 = calculatorService.sum(1.3F, 2.0F);
        System.out.println("客户端1--加法操作的结果为：" + result1);
        
       
    }
}
