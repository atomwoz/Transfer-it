package com.atomwoz.transferit.backbone;

import java.nio.charset.Charset;

public class Configuration
{

	public final static char HEADER_SLICER = '\u0000';
	public final static int DEFAULT_PORT = 5003;
	public final static int MAX_HEADER_SIZE = 576;
	public final static int MAX_PAYLOAD_SIZE = 64000;
	public final static String TRANSFER_REQ = "?transfer?";
	public final static String TRANSFER_OK = "!send!";
	public final static Charset MAIN_CHARSET = Charset.forName("utf-8");

}
