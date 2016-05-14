
package org.luwrain.cpanel;

public interface Factory
{
    Element[] getElements();
    Section createSection(Element el);
}
