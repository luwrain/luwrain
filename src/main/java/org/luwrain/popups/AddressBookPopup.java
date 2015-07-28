
package org.luwrain.popups;

import org.luwrain.core.*;

public class AddressBookPopup extends ListPopup
{

    AddressBookPopupModel addressBookModel;

    public AddressBookPopup(Luwrain luwrain,
			    String name,
			    AddressBookPopupModel addressBookModel,
			    int popupFlags)
    {
	super(luwrain, name,
	      new AddressBookPopupListModel(addressBookModel),
	      new AddressBookPopupListAppearance(luwrain, addressBookModel), popupFlags);
	this.addressBookModel = addressBookModel;
	      if (addressBookModel == null)
		  throw new NullPointerException("addressBookModel may not be null");
    }
}
