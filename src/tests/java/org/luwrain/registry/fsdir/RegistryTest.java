
package org.luwrain.registry.fsdir;

import java.io.*;
import org.junit.*;

public class RegistryTest extends Assert
{
    static private final File REGISTRY_BASE = new File("/tmp/registry-test");

    @Before public void createBase()
    {
	try {
	    REGISTRY_BASE.mkdir();
	    new File(REGISTRY_BASE, Directory.STRINGS_VALUES_FILE).createNewFile();
	    new File(REGISTRY_BASE, Directory.INTEGERS_VALUES_FILE).createNewFile();
	    new File(REGISTRY_BASE, Directory.BOOLEANS_VALUES_FILE).createNewFile();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    @After public void cleaning()
    {
	try {
	    deleteRecurse(REGISTRY_BASE);
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }

    @Test public void addDir()
    {
	final RegistryImpl registry = new RegistryImpl(REGISTRY_BASE.getAbsolutePath());
	assertTrue(registry.addDirectory("proba"));

	final File probaDir = new File(REGISTRY_BASE, "proba");
	assertTrue(probaDir.exists());
	assertTrue(probaDir.exists());
	assertTrue(new File(probaDir, Directory.STRINGS_VALUES_FILE).exists());
	assertTrue(new File(probaDir, Directory.INTEGERS_VALUES_FILE).exists());
	assertTrue(new File(probaDir, Directory.BOOLEANS_VALUES_FILE).exists());
    }

    private void deleteRecurse(File f) throws IOException
    {
	if (f.isDirectory())
	{
	    final File[] items = f.listFiles();
	    for(File i: items)
		deleteRecurse(i);
	}
	f.delete();
    }
}
