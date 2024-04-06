package com.zeal.rpc.client;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 22:49
 * @Version 1.0
 */
class DiscoveryTest {

    public static void main(String[] args) {
        Discovery discovery = new Discovery("127.0.0.1:2181");
        String address = discovery.discover();
        System.out.println("ADDER:    " + address);
    }
}