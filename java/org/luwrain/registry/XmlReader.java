/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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
    private String fileName;
    private XmlReaderOutput output;

    public XmlReader(String fileName, XmlReaderOutput output)
    {
	this.fileName = fileName;
	this.output = output;
    }

    public void readFile()  throws SAXException, IOException, ParserConfigurationException 
    {
	if (fileName == null || fileName.trim().isEmpty())
	    return;
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
		    onRegDir(current); else
		    warning("unknown tag:" + current.getTagName());
	    }
	}
	finally {
	    in.close();
	}
    }

    private void onRegDir(Element e) throws IOException
    {
	if (e == null)
	    return;
		NamedNodeMap nameMap = e.getAttributes();
		Node n = nameMap.getNamedItem("name");
		if (n == null)
		{
		    error("the tag \'regdir\' has no name, skipping");
		    return;
		}
		final String dirPath = n.getTextContent();
		Directory dir = output.findDirectoryForXmlReader(dirPath);
		if (dir == null)
		{
		    error("directory may not have path \'" + dirPath + "\'");
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
		    error("there is the value in directory \'" + dirPath + "\' without a name");
		    continue;
		}
		if (typeNode == null)
		{
		    error("the value \'" + nameNode.getTextContent() + "\' in directory \'" + dirPath + "\' has no type");
		    continue;
		}
		onRegValue(dir, dirPath,
				nameNode.getTextContent().trim(), typeNode.getTextContent().trim(), current.getTextContent().trim());
	    } else
		warning("unknown tag:" + current.getTagName());
	}
    }

    private void 		onRegValue(Directory dir, 
						String dirPath,
						String name,
						String type,
						String value)
    {
	if (dir == null || dirPath == null ||
	    name == null || type == null || value == null)
	    return;
	if (name.trim().isEmpty())
	{
		error("directory \'" + dirPath + "\' has a value with an empty name");
		return;
	}
	if (type.isEmpty())
	{
	    error("directory \'" + dirPath + "\' has a value with an empty type");
	    return;
	}
	int typeCode;
	if (type.trim().equals("string"))
	    typeCode = Registry.STRING; else
	    if (type.trim().equals("int"))
		typeCode = Registry.INTEGER; else
		if (type.trim().equals("bool"))
		    typeCode = Registry.BOOLEAN; else
		{
		    error("value \'" + name + "\' in directory \'" + dirPath + "\' has unknown type \'" + type + "\'");
		    return;
		}
	if (!output.onNewXmlValue(dir, name, typeCode, value))
	    error("the string \'" + value + "\' for value \'" + name + "\' in directory \'" + dirPath + "\' of type \'" + type + "\' has been rejected");
    }

    private void error(String msg)
    {
	Log.error("registry", "parsing " + fileName + ":" + msg);
    }

    private void warning(String msg)
    {
	Log.warning("registry", "parsing " + fileName + ":" + msg);
    }
}
