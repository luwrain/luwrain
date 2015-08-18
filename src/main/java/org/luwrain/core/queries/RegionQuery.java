
package org.luwrain.core.queries;

import org.luwrain.core.*;

public class RegionQuery extends AreaQuery
{
    private HeldData data = null;

    public RegionQuery()
    {
	super(REGION);
    }

    public void setData(HeldData data)
    {
	if (data != null)
	    throw new IllegalArgumentException("data may not be set twice");
	this.data = data;
    }

    public HeldData getData()
    {
	return data;
    }
}
