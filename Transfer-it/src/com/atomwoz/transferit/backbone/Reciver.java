package com.atomwoz.transferit.backbone;

import static com.atomwoz.transferit.backbone.Configuration.MAX_PAYLOAD_SIZE;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_OK;
import static com.atomwoz.transferit.backbone.Configuration.TRANSFER_REQ;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Reciver extends Transfer
{
	Path writePath;
	ServerSocket controlSocket;
	ByteBuffer buffer = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);

	public Reciver(int port, Path writePath, PrintWriter err)
	{
		try
		{
			controlSocket = new ServerSocket(port);
			this.writePath = writePath;
		}
		catch (IOException e)
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
			StatusPrinter sp = null;
			try
			{
				out.print("I'm waiting for incoming files....");
				out.flush();
				configureControlSocket(controlSocket.accept());
				readed = reciveControlString();
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
									Path superPath = Paths.get(writePath.toAbsolutePath().toString() + "/" + fileName)
											.normalize();
									sendControlString(TRANSFER_OK);
									try (FileChannel fc = (FileChannel) Files.newByteChannel(superPath,
											StandardOpenOption.WRITE, StandardOpenOption.CREATE))
									{
										int maximum = (int) (fileSize / MAX_PAYLOAD_SIZE);
										sp = new StatusPrinter(out, maximum);
										Thread printerThread = new Thread(sp);
										printerThread.start();
										for (int i = 0; i < maximum + 1; i++)
										{
											byte[] arr = reciveByteArray();
											buffer = ByteBuffer.allocate(arr.length);
											buffer.put(arr);
											buffer.rewind();
											fc.write(buffer);
											sp.goAhead();
											// TODO Printing
											// out.println("Transfered " + i + " " + maximum);
										}
										out.println("Transfer completed");
									}
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
				controlSocket.close();
			}
			catch (WrongHeaderException e)
			{
				err.println("[ERROR]" + e.getMessage() + "!!!!!, check this program version on file sender computer");
			}
			catch (IOException e)
			{
				err.println("[ERROR] Unexcepted network error ocured, try to restart app and recconect.");
			}
			catch (Exception e)
			{
				err.println("Unexcpeted error occured");
			}
			finally
			{
				if (socket != null)
				{
					socket.close();
				}
				if (sp != null)
				{
					sp.printerThread.interrupt();
				}
			}
		}
	}

}
