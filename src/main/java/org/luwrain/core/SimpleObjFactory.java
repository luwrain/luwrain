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

//LWR_API 1.0

package org.luwrain.core;

import java.util.function.*;

public class SimpleObjFactory implements ObjFactory
{
    protected final String extName;
    protected final String className;
protected final Supplier<Object> func;

    public SimpleObjFactory(String extName, String className, Supplier<Object> func)
    {
	NullCheck.notEmpty(extName, "extName");
	NullCheck.notNull(className, "className");
NullCheck.notNull(func, "func");
	this.extName = extName;
this.className = className;
	this.func = func;
    }

        @Override public String getExtObjName()
    {
	return extName;
    }

@Override public Object newObject(String name)
{
if (!className.equals(name))
return null;
return func.get();
}
}
