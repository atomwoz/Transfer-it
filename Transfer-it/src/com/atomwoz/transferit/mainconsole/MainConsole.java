package com.atomwoz.transferit.mainconsole;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import com.atomwoz.transferit.backbone.Transfer;

public class MainConsole
{
	static final int SERVER_MODE = 1;
	static final int CLIENT_MODE = 2;

	public static void main(String args[]) throws UnsupportedEncodingException
	{

		try (PrintWriter out = new PrintWriter(System.out, true); Scanner in = new Scanner(System.in))
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
					Transfer.doAsServer();
					break;
				}
				else if (mode == CLIENT_MODE)
				{
					Transfer.doAsClient();
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
