
package org.luwrain.cpanel;

import org.luwrain.core.*;

public class EmptySection implements Section
{
    @Override public int getDesiredRoot()
    {
	return BasicSections.NONE;
    }

    @Override public Section[] getChildSections()
    {
	return new Section[0];
    }

    @Override public Area getSectionArea(Environment environment)
    {
	return null;
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	return true;
    }

    @Override public boolean onTreeInsert(Environment environment)
    {
	return false;
    }

    @Override public boolean onTreeDelete(Environment environment)
    {
	return false;
    }

    @Override public boolean isSectionEnabled()
    {
	return true;
    }

    @Override public void refreshChildSubsections()
    {
    }

    @Override public boolean equals(Object obj)
    {
	return this == obj;
    }
}
