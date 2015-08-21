
package org.luwrain.util;

public class NullCheck
{
    static public void notNull(Object obj, String objName)
    {
	if (obj == null)
	    throw new NullPointerException(objName + "(" + obj.getClass().getName() + ") may not be null");
    }
}
