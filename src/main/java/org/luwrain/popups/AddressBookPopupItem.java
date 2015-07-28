
package org.luwrain.popups;

class AddressBookPopupItem
{
    public String title;
    public int index;

    public AddressBookPopupItem(String title, int index)
    {
	this.title = title;
	this.index = index;
	if (title == null)
	    throw new NullPointerException("title may not be null");
    }
}
