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

import java.util.ArrayList;
import java.net.URI;
import org.luwrain.core.registry.Registry;
import org.luwrain.core.Log;

abstract class MailStoringRegistry implements MailStoring
{
    private static final String GROUPS_PATH = "/org/luwrain/pim/mail/groups/";
    private static final String ACCOUNTS_PATH = "/org/luwrain/pim/mail/accounts/";

    private Registry registry;

    public MailStoringRegistry(Registry registry)
    {
	this.registry = registry;
    }

    public StoredMailGroup loadRootGroup() throws Exception
    {
	String[] groupsNames = registry.getDirectories(GROUPS_PATH);
	if (groupsNames == null || groupsNames.length == 0)
	    return null;
	for(String s: groupsNames)
	{
	    StoredMailGroupRegistry g = readMailGroup(s);
	    if (g == null)
		return null;
	    if (g.id >= 0 && g.id == g.parentId)
		return g;
	}
	return null;
    }

    public StoredMailGroup[] loadChildGroups(StoredMailGroup parentGroup) throws Exception
    {
	if (parentGroup == null)
	    return null;
	StoredMailGroupRegistry parentGroupRegistry = (StoredMailGroupRegistry)parentGroup;
	String[] groupsNames = registry.getDirectories(GROUPS_PATH);
	if (groupsNames == null || groupsNames.length == 0)
	    return new StoredMailGroup[0];
	ArrayList<StoredMailGroupRegistry> groups = new ArrayList<StoredMailGroupRegistry>();
	for(String s: groupsNames)
	{
	    StoredMailGroupRegistry g = readMailGroup(s);
	    if (g != null && g.parentId == parentGroupRegistry.id && g.id != g.parentId)
		groups.add(g);
	}
	return groups.toArray(new StoredMailGroup[groups.size()]);
    }

    public StoredMailAccount[] loadMailAccounts() throws Exception
    {
	String[] accountsIds = registry.getDirectories(ACCOUNTS_PATH);
	if (accountsIds == null || accountsIds.length == 0)
	    return new StoredMailAccount[0];
	ArrayList<StoredMailAccountRegistry> accounts = new ArrayList<StoredMailAccountRegistry>();
	for(String s: accountsIds)
	{
	    StoredMailAccountRegistry g = readMailAccount(s);
	    if (g != null)
		accounts.add(g);
	}
	return accounts.toArray(new StoredMailAccount[accounts.size()]);
    }

    public StoredMailGroup loadGroupByUri(String uri) throws Exception
    {
	URI u = new URI(uri);
	if (!u.getScheme().equals(PimManager.MAIL_GROUP_URI_SCHEME))
	    return null;
	return readMailGroup(u.getSchemeSpecificPart());
    }

    private StoredMailGroupRegistry readMailGroup(String name)
    {
	if (name == null || name.isEmpty())
	    return null;
	StoredMailGroupRegistry g = new StoredMailGroupRegistry(registry);
	try {
	    g.id = Integer.parseInt(name.trim());
	}
	catch(NumberFormatException e)
	{
	    Log.warning("pim", "registry directory \'" + GROUPS_PATH + "\' contains illegal subdirectory \'" + name + "\'");
	    return null;
	}
	final String path = GROUPS_PATH + name;
	if (registry.getTypeOf(path + "/name") != Registry.STRING)
	{
	    Log.warning("pim", "registry directory \'" + path + "\' has no proper value \'name\'");
	    return null;
	}
	g.name = registry.getString(path + "/name");
	if (registry.getTypeOf(path + "/parent") != Registry.INTEGER)
	{
	    Log.warning("pim", "registry directory \'" + path + "\' has no proper value \'parent\'");
	    return null;
	}
	    g.parentId = registry.getInteger(path + "/parent");
	if (registry.getTypeOf(path + "/expire-days") == Registry.INTEGER)
	    g.expireAfterDays = registry.getInteger(path + "/expire-days");
	if (registry.getTypeOf(path + "/order-index") == Registry.INTEGER)
	    g.orderIndex = registry.getInteger(path + "/order-index");
	return g;
    }

    private StoredMailAccountRegistry readMailAccount(String id)
    {
	if (id == null || id.isEmpty())
	    return null;
	StoredMailAccountRegistry g = new StoredMailAccountRegistry(registry);
	try {
	    g.id = Integer.parseInt(id.trim());
	}
	catch(NumberFormatException e)
	{
	    Log.warning("pim", "registry directory \'" + GROUPS_PATH + "\' contains illegal subdirectory \'" + id + "\'");
	    return null;
	}
	final String path = ACCOUNTS_PATH + id;
	if (registry.getTypeOf(path + "/name") != Registry.STRING)
	{
	    Log.warning("pim", "registry directory \'" + path + "\' has no proper value \'name\'");
	    return null;
	}
	g.name = registry.getString(path + "/name");
	if (registry.getTypeOf(path + "/type") == Registry.STRING)
	{
	    final String value = registry.getString(path + "/type").trim();
	    if (value.equals("incoming"))
		g.type = MailAccount.INCOMING; else
		if (value.equals("relay"))
		    g.type = MailAccount.RELAY; else
		    if (value.equals("relay-default"))
			g.type = MailAccount.RELAY_DEFAULT; else
		    {
			Log.error("pim", "mail account at " + path + " has invalid type \'" + value + "\'");
			return null;
		    }
	}
	if (registry.getTypeOf(path + "/protocol") == Registry.STRING)
	{
	    final String value = registry.getString(path + "/protocol").trim();
	    if (value.equals("ipop3"))
		g.protocol = MailAccount.POP3; else
		if (value.equals("pop3-ssl"))
		    g.protocol = MailAccount.POP3_SSL; else
		    if (value.equals("smtp"))
			g.protocol = MailAccount.SMTP; else
		    {
			Log.error("pim", "mail account at " + path + " has invalid protocol \'" + value + "\'");
			return null;
		    }
	}
	if (registry.getTypeOf(path + "/host") == Registry.STRING)
	    g.host = registry.getString(path + "/host");
	if (registry.getTypeOf(path + "/port") == Registry.INTEGER)
	    g.port = registry.getInteger(path + "/port");
	if (registry.getTypeOf(path + "/login") == Registry.STRING)
	    g.login = registry.getString(path + "/login");
	if (registry.getTypeOf(path + "/passwd") == Registry.STRING)
	    g.passwd = registry.getString(path + "/passwd");
	return g;
    }
}
