

package org.luwrain.util;

import java.net.*;
import java.io.*;

public class UrlMlReader
{
    public static void main(String[] args) throws Exception
    {
	if (args == null || args.length != 1)
	{
	    System.out.println("Give me one URL to read!");
	    return;
	}
	URL url = new URL(args[0]);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
	    builder.append(inputLine);
        in.close();
	SimpleMlReaderConfig config = new SimpleMlReaderConfig();
	ConsoleMlReaderListener listener = new ConsoleMlReaderListener();
	MlReader reader = new MlReader(config, listener, builder.toString());
	reader.read();
    }
}



