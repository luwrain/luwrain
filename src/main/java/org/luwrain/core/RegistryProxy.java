/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.core;

import java.lang.reflect.*;

public final class RegistryProxy
{
    static public <T> T create(Registry registry, String regDir, Class cl)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(regDir, "regDir");
	return (T) Proxy.newProxyInstance(
					  cl.getClassLoader(),
					  new Class[]{cl},
					  (object, method, args)->{
					      final String name = method.getName();
					      if (name.length() <= 3)
						  throw new IllegalArgumentException("\'" + name + "\' is too short to be a valid method name");
					      if (!name.startsWith("get") && !name.startsWith("set"))
						  throw new IllegalArgumentException("Method name should begin with \'get\' or \'set\', \'" + name + "\' is an inappropriate name");
					      final Class<?> returnType = method.getReturnType();

					      final StringBuilder b = new StringBuilder();
					      for(int i = 3;i < name.length();++i)
					      {
						  final char c = name.charAt(i);
						  if (i > 3 && Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i - 1)))
						  {
						      b.append("-");
						      b.append(Character.toLowerCase(c));
						  } else
						      b.append(Character.toLowerCase(c));
					      }
					      final String paramName = Registry.join(regDir, new String(b));

					      //Reading a string
					      if (returnType.equals(String.class) && name.startsWith("get"))
					      {
						  final int valueType = registry.getTypeOf(paramName);
						  if (valueType == Registry.INVALID)
						  {
						      if (args != null && args.length == 1 && args[0] != null &&
							  args[0].getClass().equals(java.lang.String.class))
							  return (java.lang.String)args[0];
						      throw new IllegalArgumentException("There is no registry value " + paramName);
						  }
						  if (valueType != Registry.STRING)
						      throw new IllegalArgumentException("Registry value " + paramName + " is not a string");
						  return registry.getString(paramName);
					      }

					      //Reading a boolean
					      if (returnType.equals(boolean.class) && name.startsWith("get"))
					      {
						  final int valueType = registry.getTypeOf(paramName);
						  if (valueType == Registry.INVALID)
						  {
						      if (args != null && args.length == 1 && args[0] != null
							  && args[0].getClass().equals(java.lang.Boolean.class))
							  return ((java.lang.Boolean)args[0]).booleanValue();
						      throw new IllegalArgumentException("There is no registry value " + paramName);
						  }
						  if (valueType != Registry.BOOLEAN)
						      throw new IllegalArgumentException("Registry value " + paramName + " is not a boolean");
						  return registry.getBoolean(paramName);
					      }

					      //Reading an integer
					      if (returnType.equals(int.class) && name.startsWith("get"))
					      {
						  final int valueType = registry.getTypeOf(paramName);
						  if (valueType == Registry.INVALID)
						  {
						      if (args != null && args.length == 1 && args[0] != null &&
							  args[0].getClass().equals(java.lang.Integer.class))
							  return ((java.lang.Integer)args[0]).intValue();
						      throw new IllegalArgumentException("There is no registry value " + paramName);
						  }
						  if (valueType != Registry.INTEGER)
						      throw new IllegalArgumentException("Registry value " + paramName + " is not an integer");
						  return registry.getInteger(paramName);
					      }

					      //Writing a string
					      if (name.startsWith("set") &&
						  args != null && args.length == 1 && args[0] != null && args[0].getClass().equals(java.lang.String.class))
					      {
						  if (!registry.setString(paramName, (java.lang.String)args[0]))
						      throw new Exception("Unable to write to registry the string value " + paramName);
						  return null;
					      }

					      //Writing an integer
					      if (name.startsWith("set") &&
						  args != null && args.length == 1 && args[0] != null && args[0].getClass().equals(java.lang.Integer.class))
					      {
						  if (!registry.setInteger(paramName, ((java.lang.Integer)args[0]).intValue()))
						      throw new Exception("Unable to write to registry the integer value " + paramName);
						  return null;
					      }

					      //Writing an boolean
					      if (name.startsWith("set") &&
						  args != null && args.length == 1 && args[0] != null && args[0].getClass().equals(java.lang.Boolean.class))
					      {
						  if (!registry.setBoolean(paramName, ((java.lang.Boolean)args[0]).booleanValue()))
						      throw new Exception("Unable to write to registry the boolean value " + paramName);
						  return null;
					      }






					      return null;
					  });
    }
}
