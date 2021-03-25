package com.wsy.common.protocol;

import com.wsy.common.Request;
import com.wsy.common.Response;

public interface MessageProtocol {

    Request unmarshallingRequest(byte[] data) throws Exception;

    byte[] marshallingRequest(Request request) throws Exception;

    Response unmarshallingResponse(byte[] data) throws Exception;

    byte[] marshallingResponse(Response response) throws Exception;

}
