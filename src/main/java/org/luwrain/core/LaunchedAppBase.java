
package org.luwrain.core;

import java.util.*;

class LaunchedAppBase
{
    static class AreaWrapping implements AreaWrapperFactory.Disabling
    {
	final Area origArea;
	Area securityWrapper = null;
	Area reviewWrapper = null;

	AreaWrapping(Area area)
	{
	    origArea = area;
	    securityWrapper = new SecurityAreaWrapper(origArea);
	}

	boolean containsArea(Area area)
	{
	    if (area == null)
		return false;
	    return origArea == area ||
	    securityWrapper == area ||
	    reviewWrapper == area;
	}

	Area getEffectiveArea()
	{
	    if (reviewWrapper != null)
		return reviewWrapper;
	    if (securityWrapper != null)
		return securityWrapper;
	    Log.warning("core", "there is no security wrapper for the area " + origArea.getClass().getName());
	    return origArea;
	}

	@Override public void disableAreaWrapper()
	{
	    reviewWrapper = null;
	}
    }

    final Vector<Area> popups = new Vector<Area>();
    final Vector<AreaWrapping> popupWrappings = new Vector<AreaWrapping>();

    //Returns the index of the new popup;
    int addPopup(Area popup)
    {
	NullCheck.notNull(popup, "popup");
	popups.add(popup);
	final AreaWrapping wrapping = new AreaWrapping(popup);
	popupWrappings.add(wrapping);
	return popups.size() - 1;
    }

    void closeLastPopup()
    {
	popupWrappings.remove(popupWrappings.size() - 1);
	popups.remove(popups.size() - 1);
    }

    Area getNativeAreaOfPopup(int index)
    {
	return popupWrappings.get(index).origArea;
    }

    Area getEffectiveAreaOfPopup(int index)
    {
	return popupWrappings.get(index).getEffectiveArea();
    }

    /**
     * Looks for the effective area for the specified one. Provided reference
     * may designate the required effective area, pointing either to the
     * natural area, either to the security wrapper or to the review
     * wrapper. This method may return the provided reference itself (e.g. if
     * provided reference points to the security wrapper and there is no a
     * review wrapper).
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The effective area which corresponds to the requested cell in the application layout
    */
    Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	for(AreaWrapping w: popupWrappings)
	    if (w.containsArea(area))
		return w.getEffectiveArea();
	return null;
    }

    /**
     * Returns the area wrapping object for the required area. Provided
     * reference designates a cell in the application layout, pointing either
     * to the natural area, either to the security wrapper or to the review
     * wrapper.
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The area wrapping which corresponds to  the requested cell of the application layout
     */
    AreaWrapping getAreaWrapping(Area area)
    {
	NullCheck.notNull(area, "area");
	for(AreaWrapping w: popupWrappings)
	    if (w.containsArea(area))
		return w;
	return null;
    }
}
