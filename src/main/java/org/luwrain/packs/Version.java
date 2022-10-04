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


package org.luwrain.packs;

public final class Version
{
    private Integer major = null, minor = null, release = null;

    public Version()
    {
    }

    public Version(int major, int minor, int release)
    {
	if (major < 0)
	    throw new IllegalArgumentException("major can't be negative");
	if (minor < 0)
	    throw new IllegalArgumentException("minor can't be negative");
	if (release < 0)
	    throw new IllegalArgumentException("release can't be negative");
	this.major = Integer.valueOf(major);
	this.minor = Integer.valueOf(minor);
	this.release = Integer.valueOf(release);
    }

    public int getMajor()
    {
	return major != null?major.intValue():0;
    }

    public int getMinor()
    {
	return minor != null?minor.intValue():0;
    }

    public int getRelease()
    {
	return release != null?release.intValue():0;
    }
}
