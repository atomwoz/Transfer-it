package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_OK;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_REQ;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.Scanner;

public class Reciver extends Transfer
{
	Path writePath;

	public Reciver(int port, Path writePath, PrintWriter err)
	{
		try
		{
			socket = new DatagramSocket(port);
			this.writePath = writePath;
		}
		catch (SocketException e)
		{
			err.println("[ERROR] I can't listen on port " + port
					+ ", check that it is not occupied, and you have access to open the port in system.");
		}
	}

	public void reciveFile()
	{
		try (PrintWriter out = new PrintWriter(System.out, true);
				Scanner in = new Scanner(System.in);
				PrintWriter err = new PrintWriter(System.err, true);)
		{
			String readed;
			try
			{
				out.print("I'm waiting for incoming files....");
				out.flush();
				readed = reciveString();
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
									sendString(TRANSFER_OK);
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
				socket.close();
			}
			catch (WrongHeaderException e)
			{
				err.println("[ERROR]" + e.getMessage() + "!!!!!, check this program version on file sender computer");
			}
			catch (IOException e)
			{
				err.println("[ERROR] Unexcepted network error ocured, try to restart app and recconect.");
			}
			finally
			{
				if (socket != null)
				{
					socket.close();
				}
			}
		}
	}

}
