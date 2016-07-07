
package org.luwrain.i18n;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public class PropertiesProxy
{
    static public final String CHARSET = "UTF-8";

    static public <T> T create(URL url, String prefix, Class cl) throws java.io.IOException
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(cl, "cl");
	final Properties prop = new Properties();
	final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), CHARSET));
	prop.load(reader);
	return (T) java.lang.reflect.Proxy.newProxyInstance(
							    cl.getClassLoader(),
							    new Class[]{cl},
							    (object, method, args)->{
								String name = method.getName();
								if (name.length() > 1 && Character.isLowerCase(name.charAt(0)))
								    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
								final String value = prop.getProperty(prefix + name);
								if (value == null)
								    return "#No value: " + prefix + name + "#";
								if (value.indexOf("$") < 0)
								return value.trim();
								final StringBuilder b = new StringBuilder();
								for(int i = 0;i < value.length();++i)
								    if (value.charAt(i) != '$' || i + 1 >= value.length() ||
									value.charAt(i + 1) < '0' || value.charAt(i + 1) > '9')
									b.append(value.charAt(i)); else
									b.append(args[value.charAt(++i) - '1'].toString());
								return new String(b).toString();
							    });
    }
}
