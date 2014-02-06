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

package org.luwrain.pim;

import org.luwrain.core.registry.Registry;

class StoredNewsGroupRegistry implements StoredNewsGroup
{
    public Registry registry;
    public int id;
    public String name = "";
    public String[] urls = new String[0];
    public String mediaContentType = "";
    public int orderIndex = 0;
    public int expireAfterDays = 30;

    public StoredNewsGroupRegistry(Registry registry)
    {
	this.registry = registry;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name) throws Exception
    {
	ensureValid();
	if (name == null || name.trim().isEmpty())
	    throw new ValidityException("Trying to set empty name of the news group");
	RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/title", name);
	this.name = name;
    }

    public String[] getUrls()
    {
	return urls != null?urls:new String[0];
    }

    public void setUrls(String[] urls) throws Exception
    {
	ensureValid();
	if (urls == null)
	    throw new ValidityException("Trying to set to null the list of URLs of the news group \'" + name + "\' (id=" + id + ")");
	String[] oldValues = registry.getValues(NewsStoringRegistry.GROUPS_PATH + id);
	if (oldValues != null)
	    for(String s: oldValues)
		if (s.indexOf("url") == 0)
		    RegistryUpdateWrapper.deleteValue(registry, NewsStoringRegistry.GROUPS_PATH + id + "/" + s);
	for(int i = 0;i < urls.length;++i)
	    if (!urls[i].trim().isEmpty())
		RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/url" + i, urls[i]);
	this.urls = urls;
    }

    public String getMediaContentType()
    {
	return mediaContentType != null?mediaContentType:"";
    }

    public void setMediaContentType(String value) throws Exception
    {
	ensureValid();
	if (value == null)
	    throw new ValidityException("Trying to set null value to media content type of the news group \'" + name + "\' (id=" + id + ")");
	RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/media-content-type", value);
	this.mediaContentType = value;
    }

    public int getOrderIndex()
    {
	return orderIndex;
    }

    public void setOrderIndex(int index) throws Exception
    {
	ensureValid();
	RegistryUpdateWrapper.setInteger(registry, NewsStoringRegistry.GROUPS_PATH + id + "/order-index", index);
	this.orderIndex = index;
    }

    public int getExpireAfterDays()
    {
	return expireAfterDays;
    }

    public void setExpireAfterDays(int count) throws Exception
    {
	ensureValid();
	RegistryUpdateWrapper.setInteger(registry, NewsStoringRegistry.GROUPS_PATH + id + "/expire-days", count);
	this.expireAfterDays = count;
    }

    public String toString()
    {
	return getName();
    }

    private void ensureValid() throws ValidityException
    {
	if (id < 0)
	    throw new ValidityException("Trying to change state of a news group which is not associated with the storage");
    }
}
