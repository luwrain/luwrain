
package org.luwrain.registry.fsdir;

import java.io.*;

public class ValueLIneParserTool
{
    public static void main(String[] args)
    {
	ValueLineParser parser = new ValueLineParser();
	String line = "";
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	while(true)
	{
	    System.out.print("What to parse?>");
	    try {
		line = br.readLine();
	    } 
	    catch (IOException e) 
	    {
		e.printStackTrace();
		System.exit(1);
	    }
	    if (parser.parse(line))
	    {
		System.out.println("OK!");
		System.out.println("Key: \'" + parser.key + "\'");
		System.out.println("Value: \'" + parser.value + "\'");
	    } else
		System.out.println("Error!");
	}
    }
}
