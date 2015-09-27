
package org.luwrain.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.util.*;

class UniRefList
{
    private Luwrain luwrain;
private Registry registry;
private final RegistryKeys registryKeys = new RegistryKeys();
private UniRefInfo[] uniRefs = new UniRefInfo[0];

public UniRefList(Luwrain luwrain)
{
this.luwrain = luwrain;
NullCheck.notNull(luwrain, "luwrain");
this.registry = luwrain.getRegistry();
}

UniRefInfo[] get()
{
return uniRefs;
}

void load()
{
	final RegistryAutoCheck check = new RegistryAutoCheck(registry);
	final String[] values = registry.getValues(registryKeys.desktopUniRefs());
	final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	for(String v: values)
	{
	    final String s = check.stringAny(RegistryPath.join(registryKeys.desktopUniRefs(), v), "");
	    if (s.isEmpty())
		continue;
	    final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
if (uniRef != null && !res.contains(uniRef))
	    res	   .add(uniRef);
	}
uniRefs = res.toArray(new UniRefInfo[res.size()]);
}

    void add(int pos, String[] values)
    {
	if (values == null)
	    return;
	final LinkedList<UniRefInfo> toAdd = new LinkedList<UniRefInfo>();
	for(String v: values)
	{
	    if (v == null)
		continue;
		final UniRefInfo uniRef = luwrain.getUniRefInfo(v);
		if (uniRef != null)
		    toAdd.add(uniRef);
	}
	if (toAdd.isEmpty())
	    return;
	final UniRefInfo[] newItems = toAdd.toArray(new UniRefInfo[toAdd.size()]);
	final int newPos = (pos >= 0 && pos <= uniRefs.length)?pos:0;
	final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	for(int i = 0;i < newPos;++i)
	    res.add(uniRefs[i]);
	for(UniRefInfo u: newItems)
	res.add(u);
	for(int i = newPos;i < uniRefs.length;++i)
	    res.add(uniRefs[i]);
	uniRefs = res.toArray(new UniRefInfo[res.size()]);
    }

    void save()
    {
	final String[] values = registry.getValues(registryKeys.desktopUniRefs());
	if (values != null)
	    for(String v: values)
		registry.deleteValue(RegistryPath.join(registryKeys.desktopUniRefs(), v));
	for(int i = 0;i < uniRefs.length;++i)
	{
	    String name = "" + (i + 1);
	    while (name.length() < 6)
		name = "0" + name;
	    registry.setString(RegistryPath.join(registryKeys.desktopUniRefs(), name), uniRefs[i].value());
	}
    }
}
