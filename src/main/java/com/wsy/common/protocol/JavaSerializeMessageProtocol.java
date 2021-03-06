package com.wsy.common.protocol;

import com.wsy.common.Request;
import com.wsy.common.Response;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Data
public class JavaSerializeMessageProtocol implements MessageProtocol {

	private String protocolName = JavaSerializeMessageProtocol.class.getName();

	private byte[] serialize(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);

		return bout.toByteArray();
	}

	@Override
	public byte[] marshallingRequest(Request req) throws Exception {

		return this.serialize(req);
	}

	@Override
	public Request unmarshallingRequest(byte[] data) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Request) in.readObject();
	}

	@Override
	public byte[] marshallingResponse(Response rsp) throws Exception {
		return this.serialize(rsp);
	}

	@Override
	public Response unmarshallingResponse(byte[] data) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Response) in.readObject();
	}

}
