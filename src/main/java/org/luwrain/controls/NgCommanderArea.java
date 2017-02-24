
package org.luwrain.controls;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;
import org.luwrain.base.*;

public class NgCommanderArea<E> extends ListArea
{
    static public final String PARENT_DIR = "..";
    public enum Flags {MARKING};
	public enum EntryType {REGULAR, DIR, PARENT, SYMLINK, SYMLINK_DIR, SPECIAL};

	public interface Model<E>
	{
	    E[] getEntryChildren(E entry);
	    E getEntryParent(E entry);
	}

	public interface Appearance<E>
	{
	    String getCommanderName(E entry);
	    void announceLocation(E entry);
	    String getEntryTextAppearance(E entry);
	    void announceEntry(E entry, EntryType type, boolean marked);
	}

    public interface Filter<E>
    {
	boolean commanderEntrySuits(E entry);
    }

    public interface ClickHandler
    {
	public enum Result {OPEN_DIR, OK, REJECTED};
	Result onCommanderClick(CommanderArea area, Object obj, boolean dir);
    }

public interface LoadingResult<E>
{
    void onLoadingResult(Wrapper<E>[] wrappers, int selectedIndex);
}

    static public class Params<E>
    {
	public ControlEnvironment environment;
	public NgCommanderArea.Model<E> model;
	public NgCommanderArea.Appearance<E> appearance;
	public NgCommanderArea.ClickHandler clickHandler;
	public Filter<E> filter = null;//FIXME:
	public Comparator comparator = new CommanderUtils.ByNameComparator();
	public Set<Flags> flags = EnumSet.noneOf(Flags.class);
    }

    protected final NgCommanderArea.Model<E> model;
    protected final NgCommanderArea.Appearance<E> appearance;
    protected NgCommanderArea.ClickHandler clickHandler = null;
	protected Filter<E> filter = null;
	protected Comparator comparator = null;
	protected LoadingResult<E> loadingResult = null;
	protected E currentLocation = null;

    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected FutureTask task = null;

    public NgCommanderArea(Params<E> params, E initialLocation)
    {
	super(prepareListParams(params));
	NullCheck.notNull(params.flags, "params.flags");
	this.model = params.model;
	this.appearance = params.appearance;
	this.filter = params.filter;
	this.comparator = params.comparator;
	this.clickHandler = params.clickHandler;
	super.setListClickHandler((area, index, obj)->clickImpl(index, (Wrapper<E>)obj));
	getListModel().marking = params.flags.contains(Flags.MARKING);
	//	if (!Files.isDirectory(current))
	//	    throw new IllegalArgumentException("current must address a directory");
	getListModel().load(initialLocation);
    }

    public boolean findFileName(String fileName, boolean announce)
    {
	NullCheck.notNull(fileName, "fileName");
	if (isEmpty())
	    return false;
	final Wrapper<E>[] wrappers = getListModel().wrappers;
	int index = 0;
	while(index < wrappers.length && !wrappers[index].getBaseName().equals(fileName))
	    ++index;
	if (index >= wrappers.length)
	    return false;
	select(index, false);
	if (announce)
	    appearance.announceEntry(wrappers[index].obj, wrappers[index].type, wrappers[index].isMarked());
	return true;
    }

    /*
    public Wrapper getSelectedWrapper()
    {
	return !isEmpty() && hotPointY >= 0 && hotPointY < getListModel().wrappers.length?getListModel().wrappers[hotPointY]:null;
    }
    */

    public NgCommanderArea.Model<E> getCommanderModel()
    {
	return model;
    }


    public void setCommanderFilter(Filter filter)
    {
	getListModel().filter = filter;
    }

    public void setCommanderComparator(Comparator comparator)
    {
	NullCheck.notNull(comparator, "comparator");
	getListModel().comparator = comparator;
    }

    public boolean isEmpty()
    {
	return getListModel().wrappers == null || getListModel().wrappers.length < 1;
    }

    public void open(E entry)
    {
	NullCheck.notNull(entry, "entry");
	open(entry, null);
    }

    public void open(E entry, String desiredSelected)
    {
	NullCheck.notNull(entry, "entry");
	getListModel().current = entry;
	task = new FutureTask(()->{
		final Wrapper<E>[] wrappers;
		final E[] res = model.getEntryChildren(getListModel().current);
		if (res != null)
		{
	    final LinkedList<E> filtered = new LinkedList<E>();
	    for(E e: res)
if (filter == null || filter.commanderEntrySuits(e))
		    filtered.add(e);
	    final Object[] sorted = filtered.toArray(new Object[filtered.size()]);
		Arrays.sort(sorted, comparator);
wrappers = new Wrapper[sorted.length];
	    for(int i = 0;i < sorted.length;++i)
	    {
		wrappers[i] = new Wrapper((Wrapper<E>)sorted[i], EntryType.REGULAR);
	    }
		} else
		wrappers = null;
		int index = -1;
		if (desiredSelected != null && !desiredSelected.isEmpty())
		    for(int i = 0;i < wrappers.length;++i)
			if (desiredSelected.equals(appearance.getEntryTextAppearance(wrappers[i].obj)))
			    index = i;
		loadingResult.onLoadingResult(wrappers);
	    }, null);
	    	notifyNewContent();
    }

    public void acceptNewContent(Wrapper<E>[] wrappers, int selectedIndex)
    {
	NullCheck.notNullItems(wrappers, "wrappers");
	getListModel().wrappers = wrappers;
	super.refresh();
	select(selectedIndex, false);
    }


    @Override public ListModelAdapter<E> getListModel()
    {
	return (ListModelAdapter<E>)super.getListModel();
    }

    public void setClickHandler(NgCommanderArea.ClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    @Override public void setListClickHandler(ListClickHandler clickHandler)
    {
	throw new UnsupportedOperationException("Changing list click handler for commander areas not allowed, use setClickHandler(CommanderArea.ClickHandler)instead");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case BACKSPACE:
		return onBackspace(event);
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (query.getQueryCode() == AreaQuery.CURRENT_DIR)
	{
	    final CurrentDirQuery currentDirQuery = (CurrentDirQuery)query;
	    currentDirQuery.answer(getListModel().current.toString());
	    return true;
	}
	return super.onAreaQuery(query);
    }

    @Override public String getAreaName()
    {
	if (getListModel().current == null)
	    return "-";
	return appearance.getCommanderName(getListModel().current);
    }

    protected boolean onBackspace(KeyboardEvent event)
    {
	//noContent() isn't applicable here, we should be able to leave the directory, even if it doesn't have any content
	if (getListModel().current == null)
	    return false;
	/*
	final Path parent = getListModel().current.getParent();
	if (parent == null)
	    return false;
	open(parent, getListModel().current.getFileName().toString());
	appearance.announceLocation(getListModel().current);
	*/
	return true;
    }

    protected boolean clickImpl(int index, Wrapper wrapper)
    {
	NullCheck.notNull(wrapper, "wrapper");
	/*
	final Path parent = getListModel().current.getParent();
	if (entry.type == Entry.Type.PARENT && parent != null)
	{
	    open(parent, getListModel().current.getFileName().toString());
	    appearance.announceLocation(getListModel().current);
	    return true;
	}
	if (entry.type == Entry.Type.DIR || entry.type == Entry.Type.SYMLINK_DIR)
	{
	    ClickHandler.Result res = ClickHandler.Result.OPEN_DIR;
	    if (this.clickHandler != null)
		res = this.clickHandler.onCommanderClick(this, entry.path, true);
	    switch(res)
	    {
	    case OPEN_DIR:
		open(entry.path, null);
		appearance.announceLocation(getListModel().current);
		return true;
	    case OK:
		return true;
	    case REJECTED:
		return false;
	    }
	    return false;
	} //directory
	ClickHandler.Result res = ClickHandler.Result.REJECTED;
	if (this.clickHandler != null)
	    res = this.clickHandler.onCommanderClick(this, entry.path, false);
FIXME:
	return res == ClickHandler.Result.OK?true:false;
	*/
	return false;
    }

    @Override protected String noContentStr()
    {
	return environment.getStaticStr("CommanderNoContent");
    }

    protected void notifyNewContent()
    {
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	environment.onAreaNewName(this);
    }

    /*
    static protected Entry.Type readType(Path path) throws IOException
    {
	NullCheck.notNull(path, "path");
	final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
	if (attr.isDirectory())
	    return Entry.Type.DIR;
	if (attr.isSymbolicLink())
	    if (Files.isDirectory(path))
		return Entry.Type.SYMLINK_DIR; else
		return Entry.Type.SYMLINK;
	if (attr.isRegularFile())
	    return Entry.Type.REGULAR;
	return Entry.Type.SPECIAL;
    }
    */

    static protected ListArea.Params prepareListParams(NgCommanderArea.Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.environment, "params.environment");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.comparator, "params.comparator");
	final ListArea.Params listParams = new ListArea.Params();
	listParams.environment = params.environment;
	listParams.model = new ListModelAdapter(params.model, params.filter, params.comparator);
	listParams.appearance = new ListAppearanceImpl(params.appearance);
	listParams.name = "";//Never used, getAreaName() overrides
	return listParams;
    }

    static public class Wrapper<E>
    {
	final E obj;
	final EntryType type;
	protected boolean marked;

	public Wrapper(E obj, EntryType type)
	{
	    NullCheck.notNull(obj, "obj");
	    NullCheck.notNull(type, "type");
	    this.obj = obj;
	    this.type = type;
	    this.marked = false;
	}

	public void mark()
	{
	    marked = true;
	}

	public void unmark()
	{
	    marked = false;
	}

	public void toggleMark()
	{
	    marked = !marked;
	}

	public boolean isMarked() 
{
 return marked; 
}

	@Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof Wrapper))
		return false;
	    final Wrapper<E> w = (Wrapper<E>)o;
	    return obj.equals(w.obj) && type == w.type;
	}
    }

    static public class ListAppearanceImpl<E> implements ListArea.Appearance
    {
	protected final NgCommanderArea.Appearance appearance;

	public ListAppearanceImpl(NgCommanderArea.Appearance<E> appearance)
	{
	    NullCheck.notNull(appearance, "appearance");
	    this.appearance = appearance;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Wrapper<E> wrapper = (Wrapper<E>)item;
	    appearance.announceEntry(wrapper.obj, wrapper.type, wrapper.isMarked());
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Wrapper<E> wrapper = (Wrapper<E>)item;
	    final boolean marked = wrapper.isMarked();
	    final EntryType type = wrapper.type;
	    final String name = appearance.getEntryTextAppearance(wrapper.obj);
	    final StringBuilder b = new StringBuilder();
	    b.append(marked?"*":" ");
	    switch(type)
	    {
	    case DIR:
		b.append("[");
		break;
	    case SPECIAL:
		b.append("!");
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		b.append("{");
		break;
	    default:
		b.append(" ");
	    }
	    b.append(name);
	    switch(type)
	    {
	    case DIR:
		b.append("]");
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		b.append("}");
		break;
	    }
	    return new String(b);
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 2;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return appearance.getEntryTextAppearance(((Wrapper)item).obj).length() + 2;
	}
    }

    static public class ListModelAdapter<E> implements ListArea.Model
    {
	protected final NgCommanderArea.Model<E> model;
	boolean marking = true;
	Wrapper<E>[] wrappers;//null means the content is inaccessible

	public ListModelAdapter(NgCommanderArea.Model<E> model, Filter<E> filter, Comparator comparator)
	{
	    NullCheck.notNull(model, "model");
	    this.model = model;
	}

	@Override public int getItemCount()
	{
	    return wrappers != null?wrappers.length:0;
	}

	@Override public Object getItem(int index)
	{
	    return (wrappers != null && index < wrappers.length)?wrappers[index]:null;
	}

	@Override public boolean toggleMark(int index)
	{
	    if (!marking)
		return false;
	    if (wrappers == null ||
		index < 0 || index >= wrappers.length)
		return false;
	    if (wrappers[index].type == EntryType.PARENT)
		return false;
	    wrappers[index].toggleMark();
	    return true;
	}

	@Override public void refresh()
	{
	}
    }
}
