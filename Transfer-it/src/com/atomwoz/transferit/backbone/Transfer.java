package com.atomwoz.transferit.backbone;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	public final static char HEADER_SLICER = '\u0000';
	public final static int DEFAULT_PORT = 5003;
	public final static int MAX_HEADER_SIZE = 576;
	public final static String TRANSFER_REQ = "?transfer?";
	public final static String TRANSFER_OK = "!send!";
	public final static Charset MAIN_CHARSET = Charset.forName("utf-8");

	public static void doAsServer()
	{
		try (PrintWriter out = new PrintWriter(System.out, true);
				Scanner in = new Scanner(System.in);
				PrintWriter err = new PrintWriter(System.err, true);)
		{
			out.println("Give me full path to folder where you want to save downloaded files: ");
			String localization = in.nextLine();
			File file = new File(localization);
			if (!file.isDirectory())
			{
				err.println("[ERROR] The directory you give me doesn't exists");
				return;
			}
			out.print("Write me port on which i have to listen [" + DEFAULT_PORT + "]: ");
			out.flush();
			String rawPort = in.nextLine();
			int port = DEFAULT_PORT;
			if (!rawPort.isBlank())
			{
				try
				{
					port = Integer.parseInt(rawPort);
				}
				catch (NumberFormatException e)
				{
					err.println("[ERROR] Wrong port specified");
					return;
				}
			}
			DatagramSocket ds = null;
			byte inputBuffer[] = new byte[MAX_HEADER_SIZE];
			String readed;
			try
			{
				ds = new DatagramSocket(port);
				out.print("I'm waiting for incoming files....");
				out.flush();
				ds.receive(new DatagramPacket(inputBuffer, MAX_HEADER_SIZE));
				readed = new String(inputBuffer, MAIN_CHARSET);
				if (readed.endsWith("\u0000"))
				{
					String[] header = readed.split("\u0000");
					if (header.length == 3)
					{
						try
						{
							String headerCmd = header[0];
							long fileSize = Long.parseLong(header[1]);
							String fileName = header[2];
							if (fileSize <= 0)
							{
								throw new NumberFormatException();
							}
							if (headerCmd.equals(TRANSFER_REQ))
							{
								out.println("\fAn offer of a " + fileSize + "B " + fileName
										+ " file has been recived, accept it (Yes/No):");
								String answer = in.nextLine();
								answer = answer.toLowerCase();
								if (answer.equals("yes") || answer.equals("y"))
								{
									byte outputBuffer[] = TRANSFER_OK.getBytes(MAIN_CHARSET);
									ds.send(new DatagramPacket(outputBuffer, outputBuffer.length));
									// TODO Recive file
								}
								else
								{
									out.println("Okay, the file will be rejected");
								}
							}
							else
							{
								throw new WrongHeaderException("Malformed header was recived");
							}
						}
						catch (NumberFormatException e)
						{
							throw new WrongHeaderException("Wrong file size was recived");
						}
					}
					else
					{
						throw new WrongHeaderException("Malformed header was recived");
					}
				}
				else
				{
					throw new WrongHeaderException("To big header was send");
				}
				ds.close();
			}
			catch (WrongHeaderException e)
			{
				err.println("[ERROR]" + e.getMessage() + "!!!!!, check this program version on file sender computer");
			}
			catch (SocketException e)
			{
				err.println("[ERROR] I can't listen on port " + port
						+ ", check that it is not occupied, and you have access to open the port in system.");
			}
			catch (IOException e)
			{
				err.println("[ERROR] Unexcepted network error ocured, try to restart app and recconect.");
			}
			finally
			{
				if (ds != null)
				{
					ds.close();
				}
			}
		}
	}

	public static void doAsClient() throws UnsupportedEncodingException
	{
		try (PrintWriter out = new PrintWriter(System.out, true);
				Scanner in = new Scanner(System.in);
				PrintWriter err = new PrintWriter(System.err, true);)
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
				err.println("[ERROR] I haven't access to given file");
				return;
			}
			out.print("Enter remote host (IP/name): ");
			out.flush();
			String host = in.nextLine();

			out.print("Enter remote port [" + DEFAULT_PORT + "]: ");
			out.flush();
			int port = in.nextInt();

			DatagramSocket ds;
			String header = TRANSFER_REQ + HEADER_SLICER + fileSize + HEADER_SLICER + fileName + HEADER_SLICER;
			byte outputBuffer[] = header.getBytes(MAIN_CHARSET);
			byte inputBuffer[] = new byte[MAX_HEADER_SIZE];
			try
			{
				ds = new DatagramSocket();
				ds.connect(InetAddress.getByName(host), port);
				ds.send(new DatagramPacket(outputBuffer, outputBuffer.length));
				out.println("Connected to " + host + ":" + port);
				ds.receive(new DatagramPacket(inputBuffer, inputBuffer.length));
				String ackCMD = new String(inputBuffer, MAIN_CHARSET);
				if (ackCMD.equals(TRANSFER_OK))
				{
					// TODO Send file
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
