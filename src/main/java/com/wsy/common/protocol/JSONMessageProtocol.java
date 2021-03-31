package com.wsy.common.protocol;

import com.alibaba.fastjson.JSON;
import com.wsy.common.Request;
import com.wsy.common.Response;
import lombok.Data;

/**
 * @author wangshuangyong 2021.3.30
 */

@Data
public class JSONMessageProtocol implements MessageProtocol {

    private final String protocolName = JSONMessageProtocol.class.getName();

    @Override
    public Request unmarshallingRequest(byte[] data) throws Exception {
        return JSON.parseObject(data, Request.class);
    }

    @Override
    public byte[] marshallingRequest(Request request) throws Exception {
        return JSON.toJSONBytes(request);
    }

    @Override
    public Response unmarshallingResponse(byte[] data) throws Exception {
        return JSON.parseObject(data, Response.class);
    }

    @Override
    public byte[] marshallingResponse(Response response) throws Exception {
        return JSON.toJSONBytes(response);
    }
}
