package com.atomwoz.transferit.backbone;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Transfer
{

	public static void doAsServer()
	{
		try (PrintWriter out = new PrintWriter(System.out, true); Scanner in = new Scanner(System.in))
		{
			out.print("Enter listen port (5003): ");
			out.flush();
			int port = in.nextInt();
			DatagramSocket ds;
			byte inputBuffer[] = new byte[512];
			try
			{
				ds = new DatagramSocket(port);
				ds.receive(new DatagramPacket(inputBuffer, 512));
				out.println("Recived " + new String(inputBuffer));
				ds.close();
			}
			catch (IOException e)
			{
				out.println("[ERROR] I can't listen on port " + port + ".");
			}
		}
	}

	public static void doAsClient() throws UnsupportedEncodingException
	{
		try (PrintWriter out = new PrintWriter(System.out, true); Scanner in = new Scanner(System.in))
		{
			out.println("Give localization to file you want to send: ");
			String localization = in.nextLine();
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
				out.println("[ERROR] I haven't access to given file");
				return;
			}
			out.print("Enter remote host (IP/name): ");
			out.flush();
			String host = in.nextLine();

			out.print("Enter remote port: ");
			out.flush();
			int port = in.nextInt();

			DatagramSocket ds;
			String header = "?transfer?" + "\u0000" + fileSize + "\u0000" + fileName + "\u0000";
			byte outputBuffer[] = header.getBytes("utf-8");
			try
			{
				ds = new DatagramSocket();
				ds.connect(InetAddress.getByName("localhost"), port);
				ds.send(new DatagramPacket(outputBuffer, outputBuffer.length));
				out.println("Connected to " + host + ":" + port);
			}
			catch (IOException e)
			{
				out.println("[ERROR] I can't connect to " + host + ":" + port + ".");
			}
		}
	}

}
