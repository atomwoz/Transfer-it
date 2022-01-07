package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.MAIN_CHARSET;
import static com.atomwoz.transferit.backbone.Configuration.MAX_HEADER_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

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
	Socket controlSocket;
	DatagramSocket socket;
	SocketAddress sockAddr;
	private PrintWriter out;
	private OutputStream os;
	private InputStream is;
	private Scanner in;

	public Transfer()
	{

	}

	public Transfer(SocketAddress sockAddr)
	{
		this.sockAddr = sockAddr;
	}

	protected void configureControlSocket(Socket socket) throws IOException
	{
		this.controlSocket = socket;

		os = socket.getOutputStream();
		is = socket.getInputStream();
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter(socket.getOutputStream(), true);

	}

	protected void sendString(String str) throws IOException
	{
		byte inputBuffer[] = str.getBytes(MAIN_CHARSET);
		socket.send(new DatagramPacket(inputBuffer, inputBuffer.length, sockAddr));
	}

	protected String reciveString() throws IOException
	{
		byte inputBuffer[] = new byte[MAX_HEADER_SIZE];
		socket.receive(new DatagramPacket(inputBuffer, MAX_HEADER_SIZE));
		return new String(inputBuffer, MAIN_CHARSET);
	}

	protected void sendArray(byte array[], int end) throws IOException
	{
		os.write(array, 0, end);
	}

	protected void sendControlString(String str) throws IOException
	{
		out.println(str);
	}

	protected byte[] reciveByteArray() throws IOException
	{
		return is.readAllBytes();
	}

	protected String reciveControlString() throws IOException
	{
		return in.nextLine();
	}

}
