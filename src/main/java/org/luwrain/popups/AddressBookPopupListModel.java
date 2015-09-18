/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
