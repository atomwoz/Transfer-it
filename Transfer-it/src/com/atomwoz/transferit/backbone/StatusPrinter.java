package com.atomwoz.transferit.backbone;

import java.io.PrintWriter;

public class StatusPrinter implements Runnable
{

	private PrintWriter out;
	private int maximum;
	private int iteration;
	Thread printerThread;

	public StatusPrinter(PrintWriter out, int maximum)
	{
		this.out = out;
		this.maximum = maximum;
	}

	@Override
	public void run()
	{
		printerThread = Thread.currentThread();
		while (true)
		{

			float procent = iteration / maximum;
			procent *= 100;
			out.println("\fTransfered " + procent + "% " + iteration + " " + maximum);
			synchronized (this)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		}
	}

	public synchronized void goAhead()
	{
		iteration++;
		notify();
	}

	private int map(int value, int inputBegin, int inputEnd, int outputBegin, int outputEnd)
	{
		return outputBegin + ((outputEnd - outputBegin) / (inputEnd - inputBegin)) * (value - inputBegin);
	}

}
