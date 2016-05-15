
package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.cpanel.*;

class TreeItem 
{
    Element el;
    Factory factory;
    Section sect = null;
    final LinkedList<Element> children = new LinkedList<Element>();

    TreeItem(Element el, Factory factory)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(factory, "factory");
	this.el = el;
	this.factory = factory;
    }
}
