/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;
import java.util.regex.*;

import org.luwrain.base.*;

final class CoreProperties2
{
    private Provider[] providers = new Provider[0];

    /**
     * Sets new list of providers.
     *
     * @param p The new providers
     * @return True if all providers are valid and the current list was updated, FALSE otherwise
     */
    boolean setProviders(CorePropertiesProvider[] p)
    {
	NullCheck.notNullItems(p, "p");
	if (p.length == 0)
	    return false;
	final Provider[] newProviders = new Provider[p.length];
	for(int i = 0;i < p.length;++i)
	    newProviders[i] = new Provider(p[i]);
	for(Provider pr: newProviders)
	    if (!pr.isValid())
		return false;
	this.providers = newProviders;
	return true;
    }

    /**
     * Returns a value of the property.
     *
     * @param propName A name of the property, may not be empty
     * @returns A value of the property or {@code null}, if there is no such property
     */
    String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	for(Provider p: providers)
	    if (p.matches(propName))
		return p.provider.getProperty(propName);
	return null;
	}

    static private final class Provider
    {
	final CorePropertiesProvider provider;
	final Pattern[] patterns;

	Provider(CorePropertiesProvider provider)
	{
	    NullCheck.notNull(provider, "provider");
	    this.provider = provider;
	    final String[] regex = provider.getPropertiesRegex();
	    NullCheck.notNullItems(regex, "regex");
	    final List<Pattern> patterns = new LinkedList();
	    for (String r: regex)
		if (!r.isEmpty())
		    patterns.add(Pattern.compile(r));//Pattern.CASE_INSENSITIVE	    this.patterns = patterns.toArray(new Pattern[patterns.size()]);
	    this.patterns = patterns.toArray(new Pattern[patterns.size()]);
	}

	boolean isValid()
	{
	    return patterns.length > 0;
	}

	boolean matches(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    for(Pattern p: patterns)
	    {
	    	final Matcher matcher = p.matcher(propName);
		if (matcher.find())
		    return true;
	    }
	    return false;
	}
    }
}
