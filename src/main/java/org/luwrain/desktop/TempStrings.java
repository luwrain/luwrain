
package org.luwrain.desktop;

class TempStrings implements Strings
{
    private Strings strings = null;

    public void setStrings(Strings strings)
    {
	this.strings = strings;
	System.out.println(this.strings.appName());
    }

    @Override public String appName()
    {
	return strings != null?strings.appName():"Desktop";
    }
}
