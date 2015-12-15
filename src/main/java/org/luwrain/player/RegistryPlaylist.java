
package org.luwrain.player;

import org.luwrain.core.*;

class RegistryPlaylist extends PlaylistBase
{
    private interface Params
    {
	String getTitle(String defValue);
	String getUrl(String defValue);
	boolean getStreaming(boolean defValue);
	boolean getHasBookmark(boolean defValue);
    }

    private Registry registry;

    RegistryPlaylist(Registry registry)
    {
	this.registry = registry;
	NullCheck.notNull(registry, "registry");
    }

    boolean init(String path)
    {
	NullCheck.notNull(path, "path");
	final Params params = RegistryProxy.create(registry, path, Params.class);
	try {
	    title = params.getTitle("");
	    url = params.getUrl("");
	    streaming = params.getStreaming(false);
	    hasBookmark = params.getHasBookmark(false);

	    if (url != null && !url.toLowerCase().endsWith(".m3u"))
	    {
		items.add(url);
		url = "";
	    }
	    return title != null;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return false;
	}
    }
}
