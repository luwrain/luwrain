
package org.luwrain.cpanel;

public class StandardElements
{
    static class StandardElement implements Element
    {
	private Element parent;
	private String value;

	StandardElement(Element parent, String value)
	{
	    this.parent = parent;
	    this.value = value;
	}

	@Override public Element getParentElement()
	{
	    return parent;
	}

	@Override public String toString()
	{
	    return value;
	}

	@Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof StandardElement))
		return false;

	    return value == ((StandardElement)o).value;
	}

	@Override public int hashCode()
	{
	    return value.hashCode();
	}
    }

    static public final Element ROOT = new StandardElement(null, StandardElement.class.getName() + ":ROOT");
    static public final Element APPLICATIONS = new StandardElement(ROOT, StandardElement.class.getName() + ":APPLICATIONS");
    static public final Element KEYBOARD = new StandardElement(ROOT, StandardElement.class.getName() + ":KEYBOARD");
    static public final Element SOUNDS = new StandardElement(ROOT, StandardElement.class.getName() + ":SOUNDS");
    static public final Element SPEECH = new StandardElement(ROOT, StandardElement.class.getName() + ":SPEECH");
    static public final Element NETWORK = new StandardElement(ROOT, StandardElement.class.getName() + ":NETWORD");
    static public final Element HARDWARE = new StandardElement(ROOT, StandardElement.class.getName() + ":HARDWARE");
    static public final Element UI = new StandardElement(ROOT, StandardElement.class.getName() + ":UI");
    static public final Element EXTENSIONS = new StandardElement(ROOT, StandardElement.class.getName() + ":EXTENSIONS");
    static public final Element WORKERS = new StandardElement(ROOT, StandardElement.class.getName() + ":WORKERS");
}
