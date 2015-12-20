/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

public interface Settings
{
    static public RegistryKeys keys = new RegistryKeys();

    public interface UserInterface
    {
	String getLaunchGreeting(String defValue);
	boolean getFilePopupSkipHidden(boolean defValue);
	void setLaunchGreeting(String value);
    }

    static public UserInterface createUserInterface(Registry registry)
    {
	return RegistryProxy.create(registry, keys.ui(), UserInterface.class);
    }
}
