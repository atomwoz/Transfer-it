package com.atomwoz.transferit.mainconsole;

import java.io.PrintWriter;
import java.util.Scanner;

import com.atomwoz.transferit.backbone.Transfer;

public class MainConsole
{
	static final int SERVER_MODE = 1;
	static final int CLIENT_MODE = 2;

	public static void main(String args[])
	{

		while (true)
		{
			PrintWriter out = new PrintWriter(System.out);
			Scanner in = new Scanner(System.in);
			out.println("Welcome in transfer it!!. Choose work mode");
			out.println(SERVER_MODE + ". Recive file");
			out.println(CLIENT_MODE + ". Send file");
			int mode = in.nextInt();
			if (mode == SERVER_MODE)
			{
				Transfer.doAsServer();
			}
			else if (mode == CLIENT_MODE)
			{
				Transfer.doAsClient();
			}
			else
			{
				out.println("Wrong option specified, try again");
				for (int i = 0; i < 3; i++)
					out.println();
				continue;
			}
		}
	}
}
