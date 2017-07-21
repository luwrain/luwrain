/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.extensions;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class ClassLoader extends java.lang.ClassLoader 
{
    ClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    @Override public Class loadClass(String name) throws ClassNotFoundException
    {
        if (name.startsWith("fixme"))
	{
            return getClass(name);
        }
        return super.loadClass(name);
    }

    private Class getClass(String name) throws ClassNotFoundException 
    {
	String file = name.replace('.', File.separatorChar) + ".class";
	byte[] b = null;
	try {
	    b = loadClassFileData(file);
            Class c = defineClass(name, b, 0, b.length);
            resolveClass(c);
            return c;
        } 
	catch (IOException e) 
	{
            e.printStackTrace();
            return null;
        }
    }

    private byte[] loadClassFileData(String name) throws IOException 
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        in.readFully(buff);
        in.close();
        return buff;
    }
}
