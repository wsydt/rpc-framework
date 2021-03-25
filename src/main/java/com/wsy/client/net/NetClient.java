package com.wsy.client.net;

import com.wsy.discovery.ServiceInfo;

/**
 * @author wangshuangyong 2021.3.22
 */

public interface NetClient {

    byte[] sendRequest(byte[] data, ServiceInfo service) throws Throwable;
}
