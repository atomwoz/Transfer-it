package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.HEADER_SLICER;
import static com.atomwoz.transferit.backbone.Configuration.MAX_PAYLOAD_SIZE;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_OK;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_REQ;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Sender extends Transfer
{
	String localization;
	String host;
	int port;
	ByteBuffer buffer = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);

	public Sender(String localization, String host, int port)
	{

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

			SeekableByteChannel sendFileChannel;
			long fileSize = 0;
			String fileName;
			try
			{
				Path filePath = Paths.get(localization);
				sendFileChannel = Files.newByteChannel(filePath);
				fileSize = sendFileChannel.size();
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
				configureControlSocket(new Socket(host, port));
				out.println("Connected to " + controlSocket.getInetAddress().getHostAddress());
				sendControlString(header);
				String ackCMD = reciveControlString();
				if (ackCMD.equals(TRANSFER_OK))
				{
					int num = 0;
					do
					{
						num = sendFileChannel.read(buffer);
						if (num > 0)
						{

							byte arr[] = buffer.array();
							buffer.rewind();
							// out.println(num + "::" + arrToStr(arr));
							sendArray(arr, num);
						}
					} while (num >= 0);
				}
				else
				{
					err.println("[ERROR] Malformed message recived");
				}
			}
			catch (SocketException e)
			{
				err.println("[ERROR] I can't open socket, please check system permmisions");
			}
			catch (UnknownHostException e)
			{
				err.println("[ERROR] I can't find given host, please try again.");
			}
			catch (IOException e)
			{
				err.println("[ERROR] Unexcepted network error ocured, try to restart app and recconect. ");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				try
				{
					sendFileChannel.close();
					controlSocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	static String arrToStr(byte arr[])
	{
		Byte b;
		String str = "";
		for (var a : arr)
		{
			b = a;
			str += b.toString();
		}
		return str;
	}

}
