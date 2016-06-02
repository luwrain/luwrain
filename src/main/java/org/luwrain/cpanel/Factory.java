
package org.luwrain.cpanel;

public interface Factory
{
    Element[] getElements();
    Element[] getOnDemandElements(Element parent);
    Section createSection(Element el);
}
