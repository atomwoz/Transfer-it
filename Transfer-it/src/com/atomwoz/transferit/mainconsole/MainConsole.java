package com.atomwoz.transferit.mainconsole;

import static com.atomwoz.transferit.backbone.Configuration.DEFAULT_PORT;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Scanner;

import com.atomwoz.transferit.backbone.Reciver;
import com.atomwoz.transferit.backbone.Sender;

public class MainConsole
{
	static final int SERVER_MODE = 1;
	static final int CLIENT_MODE = 2;

	public static void main(String args[]) throws UnsupportedEncodingException
	{

		try (PrintWriter out = new PrintWriter(System.out, true);
				Scanner in = new Scanner(System.in);
				PrintWriter err = new PrintWriter(System.err, true);)
		{
			while (true)
			{
				int mode;
				out.println("Welcome in transfer it!!. Choose work mode");
				out.println(SERVER_MODE + ". Recive file");
				out.println(CLIENT_MODE + ". Send file");
				try
				{
					mode = Integer.parseInt(in.nextLine());
				}
				catch (Exception e)
				{
					out.println("Wrong option specified, try again");
					for (int i = 0; i < 3; i++)
						out.println();
					continue;
				}
				if (mode == SERVER_MODE)
				{
					out.println("Give me full path to folder where you want to save downloaded files: ");
					String localization = in.nextLine();
					// String localization = "I:";
					File file = new File(localization);
					if (!file.isDirectory())
					{
						err.println("[ERROR] The directory you give me doesn't exists");
						return;
					}
					out.print("Write me port on which i have to listen [" + DEFAULT_PORT + "]: ");
					out.flush();
					String rawPort = in.nextLine();
					// String rawPort = "";
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
					Reciver reciver = new Reciver(port, Paths.get(localization), err);
					reciver.reciveFile();
					break;
				}
				else if (mode == CLIENT_MODE)
				{
					out.println("Give localization to file you want to send: ");
					String localization = in.nextLine();
					// String localization = "I:\\Obrazy\\ubcd537.iso";

					out.print("Enter remote host (IP/name): ");
					out.flush();
					String host = in.nextLine();
					// String host = "localhost";

					out.print("Enter remote port [" + DEFAULT_PORT + "]: ");
					out.flush();
					String rawPort = in.nextLine();
					// String rawPort = "";
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
					Sender sender = new Sender(localization, host, port);
					sender.sendFile();

					break;
				}
				else
				{
					out.println("Wrong option specified, try again");
					for (int i = 0; i < 3; i++)
						out.println();
				}
			}
		}
	}
}
//FIXME Shouting can't open a socket when can't write/read from/to file.
//FIXME Mappiing values
