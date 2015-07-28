
package org.luwrain.popups;

public interface AddressBookPopupModel
{
    boolean switchToNextSection();
    boolean switchToPrevSection();
    String getCurrentSectionName();
    int getContactCount();
    String getContactTitle(int index);
    boolean isContactMarked(int index);
    boolean toggleContactMark(int index);
    String getContactDetails(int index);
    boolean markContactWithDetails(int index, String[] details);
}
