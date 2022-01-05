package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.MAIN_CHARSET;
import static com.atomwoz.transferit.backbone.Configuration.MAX_MESSAGE_SIZE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class WrongHeaderException extends Exception
{

	private static final long serialVersionUID = 6528973447627399248L;

	public WrongHeaderException(String message)
	{
		super(message);
	}
}

/**
 * 
 * @author atomwoz
 */
public class Transfer
{

	DatagramSocket socket;

	protected void sendString(String str) throws IOException
	{
		byte inputBuffer[] = str.getBytes(MAIN_CHARSET);
		socket.send(new DatagramPacket(inputBuffer, inputBuffer.length));
	}

	protected String reciveString() throws IOException
	{
		byte inputBuffer[] = new byte[MAX_MESSAGE_SIZE];
		socket.receive(new DatagramPacket(inputBuffer, MAX_MESSAGE_SIZE));
		return new String(inputBuffer, MAIN_CHARSET);
	}
}
