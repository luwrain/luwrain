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

package org.luwrain.core;

import java.util.*;
import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class Registry
{
    public static final int INVALID = -1;
    public static final int DIRECTORY = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    private static Registry instance = null; //For static methods;

    class Value
    {
	public String name = new String();
	public int type = INVALID;
	public String strValue;
	public int intValue = 0;
	public boolean boolValue;
    }

    class Directory
    {
	public String path = new String();
	public Vector<Value> values = new Vector<Value>();

	public Value findValue(String name)
	{
	    if (name == null)
		return null;
	    for(int i = 0;i < values.size();i++)
		if (values.get(i).name.equals(name))
		    return values.get(i);
	    return null;
	}
    }

    private Vector<Directory> directories = new Vector<Directory>();

    public Registry()
    {
    }

    public boolean hasValue(String path)
    {
	return getTypeOf(path) != INVALID;
    }

    public int getTypeOf(String path)
    {
	Value value = findValue(getDirectoryPath(path), getValueName(path));
	return value != null?value.type:INVALID;
    }

    public int getInteger(String path)
    {
	Value value = findValue(getDirectoryPath(path), getValueName(path));
	if (value == null || value.type != INTEGER)
	    return 0;
	return value.intValue;
    }

    public String getString(String path)
    {
	Value value = findValue(getDirectoryPath(path), getValueName(path));
	if (value == null || value.type != STRING)
	    return "";
	return value.strValue;
    }

    public boolean getBool(String path)
    {
	Value value = findValue(getDirectoryPath(path), getValueName(path));
	if (value == null || value.type != BOOLEAN)
	    return false;
	return value.boolValue;
    }

    private Directory findDirectory(String path)
    {
	if (directories == null)
	    return null;
	for(int i = 0;i < directories.size();i++)
	    if (directories.get(i).path.equals(path))
		return directories.get(i);
	return null;
    }

    private Value findValue(String directoryPath, String valueName)
    {
	//	Log.debug("registry", directoryPath + ":" + valueName);
	Directory dir = findDirectory(directoryPath);
	if (dir == null)
	    return null;
	return dir.findValue(valueName);
    }

    //FIXME:Maybe it is a good idea to remove any slash doubling;
    private String getDirectoryPath(String path)
    {
	if (path == null)
	    return new String();
	String res = path.trim();
	if (res.isEmpty() || res.equals("/"))
	    return "/";
	int ending = res.length() - 1;
	while (ending > 0 && res.charAt(ending) != '/')
	    ending--;
	if (ending < 1)
	    return "/";
	return res.substring(0, ending);
    }

    private String getValueName(String path)
    {
	if (path == null)
	    return new String();
	String res = path.trim();
	if (res.isEmpty() || res.equals("/"))
	    return new String();
	int ending = res.length() - 1;
	while (ending > 0 && res.charAt(ending) != '/')
	    ending--;
	if (res.charAt(ending) == '/')
	    return res.substring(ending + 1, res.length());
	return res.substring(ending, res.length());
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
		    Log.error("registry", "parsing " + fileName + ":unknown tag:" + current.getTagName());
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
		    Log.error("registry", "parsing " + fileName + ":the tag \'regdir\' has no name");
		    return;
		}
		Directory dir = findDirectory(n.getTextContent());
		if (dir == null)
		{
		    dir = new Directory();
		    dir.path = n.getTextContent();
		    directories.add(dir);
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
		    Log.error("registry", "parsing " + fileName + ":the value in directory \'" + dir.path + "\' has no name");
		    continue;
		}
		if (typeNode == null)
		{
		    Log.error("registry", "parsing " + fileName + ":value in directory \'" + dir.path + "\' has no name");
		    continue;
		}
		processRegValue(fileName, dir, nameNode.getTextContent().trim(),
				typeNode.getTextContent().trim(), current.getTextContent().trim());
	    } else
		Log.error("registry", "parsing " + fileName + ":unknown tag:" + current.getTagName());
	}
    }

    private void 		processRegValue(String fileName,
						Directory dir, 
						String name,
						String type,
						String value)
    {
	if (fileName == null || dir == null ||
	    name == null || type == null || value == null)
	    return;
	if (name.isEmpty())
	{
		Log.error("registry", "parsing " + fileName + ":directory \'" + dir.path + "\' has a value with an empty name");
		return;
	}
	if (type.isEmpty())
	{
		Log.error("registry", "parsing " + fileName + ":directory \'" + dir.path + "\' has a value with an empty type");
		return;
	}
	if (type.equals("string"))
	{
	    Value v = dir.findValue(name);
	    if (v == null)
	    {
		v = new Value();
		v.name = name;
		dir.values.add(v);
	    }
	    v.type = STRING;
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
		    Log.error("registry", "parsing " + fileName + ":directory \'" + dir.path + "\' has boolean option \'" + name + "\' with an invalid value \'" + value + "\'");
		    return;
		}
	    Value v = dir.findValue(name);
	    if (v == null)
	    {
		v = new Value();
		v.name = name;
		dir.values.add(v);
	    }
	    v.type = BOOLEAN;
	    v.boolValue = res;
	    return;
	}
	if (type.equals("int"))
	{
	    int res = Integer.parseInt(value);
	    Value v = dir.findValue(name);
	    if (v == null)
	    {
		v = new Value();
		v.name = name;
		dir.values.add(v);
	    }
	    v.type = INTEGER;
	    v.intValue = res;
	    return;
	}
    }

    public static boolean setInstance(Registry newInstance)
    {
	if (instance != null)
	    return false;
	instance = newInstance;
	return true;
    }

    public static int typeOf(String path)
    {
	if (instance == null)
	    return INVALID;
	return instance.getTypeOf(path);
    }

    public static String string(String path)
    {
	if (instance == null)
	    return "";
	return instance.getString(path);
    }

    public static int integer(String path)
    {
	if (instance == null)
	    return 0;
	return instance.getInteger(path);
    }

    public static boolean bool(String path)
    {
	if (instance == null)
	    return false;
	return instance.getBool(path);
    }
}
