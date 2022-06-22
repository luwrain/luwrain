/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
