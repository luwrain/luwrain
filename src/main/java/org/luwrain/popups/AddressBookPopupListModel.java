
package org.luwrain.popups;

import org.luwrain.controls.ListModel;

class AddressBookPopupListModel implements     ListModel
{
    private AddressBookPopupModel addressBookModel;

    public AddressBookPopupListModel(AddressBookPopupModel addressBookModel)
    {
	this.addressBookModel = addressBookModel;
	if (addressBookModel == null)
	    throw new NullPointerException("addressBookModel may not be null");
	refresh();
    }

    @Override public int getItemCount()
    {
	return addressBookModel.getContactCount();
    }

    @Override public Object getItem(int index)
    {
	return new AddressBookPopupItem(addressBookModel.getContactTitle(index), index);
    }

    @Override public boolean toggleMark(int index)
    {
	return addressBookModel.toggleContactMark(index);
    }

    @Override public void refresh()
    {
    }
}
