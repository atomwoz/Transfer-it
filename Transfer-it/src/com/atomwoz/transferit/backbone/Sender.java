package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.HEADER_SLICER;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_OK;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_REQ;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Sender extends Transfer
{
	String localization;
	String host;
	int port;

	public Sender(String localization, String host, int port) throws UnknownHostException, SocketException
	{

		socket = new DatagramSocket();
		socket.connect(InetAddress.getByName(host), port);
		this.host = host;
		this.port = port;
		this.localization = localization;
	}

	public void sendFile()
	{
		try (PrintWriter out = new PrintWriter(System.out, true);
				Scanner in = new Scanner(System.in);
				PrintWriter err = new PrintWriter(System.err, true);)
		{
			FileChannel sendChannel;
			long fileSize = 0;
			String fileName;
			try
			{
				Path filePath = Paths.get(localization);
				sendChannel = FileChannel.open(filePath);
				fileSize = sendChannel.size();
				fileName = filePath.getFileName().toString();
			}
			catch (IOException e1)
			{
				err.println("[ERROR] I haven't access to given file");
				return;
			}
			String header = TRANSFER_REQ + HEADER_SLICER + fileSize + HEADER_SLICER + fileName + HEADER_SLICER;
			try
			{

				sendString(header);
				out.println("Connected to " + host + ":" + port);
				String ackCMD = reciveString();
				if (ackCMD.equals(TRANSFER_OK))
				{
					out.println("Starting file transmission..");
				}
				else
				{
					err.println("[ERROR] Malformed message recived");
				}
			}
			catch (IOException e)
			{
				err.println("[ERROR] I can't connect to " + host + ":" + port + ".");
			}
		}
	}

}
