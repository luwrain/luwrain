
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

    TreeItem(Element el)
    {
	NullCheck.notNull(el, "el");
	this.el = el;
    }
}
