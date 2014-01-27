/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core.registry;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.luwrain.core.Log;

public class XmlReader
{
    private Directory root;

    public XmlReader(Directory root)
    {
	this.root = root;
    }

    public void readFile(String fileName)  throws SAXException, IOException, ParserConfigurationException 
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = builder.parse(new InputSource(in));
	    NodeList nodes = document.getDocumentElement().getChildNodes();
	    for (int i = 0;i < nodes.getLength();i++)
	    {
		Node node = nodes.item(i);
		if (node.getNodeType() != Node.ELEMENT_NODE)
		    continue;
		Element current = (Element)node;
		if (current.getTagName().equals("regdir"))
		    processRegDir(fileName, current); else
		    Log.warning("registry", "parsing " + fileName + ":unknown tag:" + current.getTagName());
	    }
	}
	finally {
	    in.close();;
	}
    }

    private void processRegDir(String fileName, Element e) throws IOException
    {
	if (e == null)
	    return;
		NamedNodeMap nameMap = e.getAttributes();
		Node n = nameMap.getNamedItem("name");
		if (n == null)
		{
		    Log.error("registry", "parsing " + fileName + ":the tag \'regdir\' has no name, skipping");
		    return;
		}
		final String dirPath = n.getTextContent();
		Directory dir = getDirectory(fileName, dirPath);
		if (dir == null)
		{
		    Log.error("registry", "parsing " + fileName + ":the ambiguous directory name '" + n.getTextContent() + "\'");
		    return;
		}
		NodeList nodes = e.getChildNodes();
	for (int i = 0;i < nodes.getLength();i++) 
	{
	    Node node = nodes.item(i);
	    if (node.getNodeType() != Node.ELEMENT_NODE)
		continue;
	    Element current = (Element)node;
	    if (current.getTagName().equals("regvalue"))
	    {
		NamedNodeMap attr = current.getAttributes();
		Node nameNode = attr.getNamedItem("name");
		Node typeNode = attr.getNamedItem("type");
		if (nameNode == null)
		{
		    Log.error("registry", "parsing " + fileName + ":the value in directory \'" + dirPath + "\' has no name");
		    continue;
		}
		if (typeNode == null)
		{
		    Log.error("registry", "parsing " + fileName + ":value in directory \'" + dirPath + "\' has no name");
		    continue;
		}
		processRegValue(fileName, dir, dirPath,
				nameNode.getTextContent().trim(), typeNode.getTextContent().trim(), current.getTextContent().trim());
	    } else
		Log.warning("registry", "parsing " + fileName + ":unknown tag:" + current.getTagName());
	}
    }

    private void 		processRegValue(String fileName,
						Directory dir, 
						String dirPath,
						String name,
						String type,
						String value)
    {
	if (fileName == null || dir == null ||
	    name == null || type == null || value == null)
	    return;
	if (name.isEmpty())
	{
		Log.error("registry", "parsing " + fileName + ":directory \'" + dirPath + "\' has a value with an empty name");
		return;
	}
	if (type.isEmpty())
	{
		Log.error("registry", "parsing " + fileName + ":directory \'" + dirPath + "\' has a value with an empty type");
		return;
	}
	if (type.equals("string"))
	{
	    Value v = dir.getValue(name);
	    //FIXME:Warning if was not string;
	    v.type = Registry.STRING;
	    v.strValue = value;
	    return;
	}
	if (type.equals("bool"))
	{
	    boolean res = false;
	    if (value.equals("TRUE") || value.equals("True") || value.equals("true"))
		res = true; else 
		if (value.equals("FALSE") || value.equals("False") || value.equals("false"))
		    res = false; else
		{
		    Log.error("registry", "parsing " + fileName + ":directory \'" + dirPath + "\' has boolean option \'" + name + "\' with an invalid value \'" + value + "\'");
		    return;
		}
	    Value v = dir.getValue(name);
	    v.type = Registry.BOOLEAN;
	    //FIXME:Warning if was not boolean;
	    v.boolValue = res;
	    return;
	}
	if (type.equals("int"))
	{
	    int res = Integer.parseInt(value);
	    Value v = dir.getValue(name);
	    //FIXME:Warning if was not integer;
	    v.type = Registry.INTEGER;
	    v.intValue = res;
	    return;
	}
    }

    private Directory getDirectory(String fileName, String pathStr)
    {
	//FIXME:
	return null;
    }
}
