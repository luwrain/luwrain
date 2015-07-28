
package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.controls.ListItemAppearance;

class AddressBookPopupListAppearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private AddressBookPopupModel addressBookModel;

    public AddressBookPopupListAppearance(Luwrain luwrain, AddressBookPopupModel addressBookModel)
    {
	this.luwrain = luwrain;
	this.addressBookModel = addressBookModel;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (addressBookModel == null)
	    throw new NullPointerException("addressBookModel may not be null");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null || !(item instanceof AddressBookPopupItem))
	    return;
	final AddressBookPopupItem it = (AddressBookPopupItem)item;
	if (addressBookModel.isContactMarked(it.index))
	    luwrain.say("selected " + it.title); else //FIXME:
	    luwrain.say(it.title);
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null || !(item instanceof AddressBookPopupItem))
	    return "  ";
	final AddressBookPopupItem it = (AddressBookPopupItem)item;
	if (addressBookModel.isContactMarked(it.index))
	    return "* " + it.title;
	return "  " + it.title;
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 2;
    }

    @Override public int getObservableRightBound(Object item)
    {
	if (item == null || !(item instanceof AddressBookPopupItem))
	    return 2;
	final AddressBookPopupItem it = (AddressBookPopupItem)item;
	return 2 + it.title.length();
    }
}
