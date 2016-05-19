
package org.luwrain.cpanel;

public class SimpleElement implements Element
{
    private Element parent;
    private String value;

    public SimpleElement(Element parent, String value)
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
	if (o == null || !(o instanceof SimpleElement))
	    return false;
	return value == ((SimpleElement)o).value;
    }

    @Override public int hashCode()
    {
	return value.hashCode();
    }
}
