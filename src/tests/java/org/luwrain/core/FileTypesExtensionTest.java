
package org.luwrain.core;

import org.junit.*;

public class FileTypesExtensionTest extends Assert
{
    @Test public void main()
    {
	assertTrue(FileTypes.getExtension(".proba").equals(""));
	assertTrue(FileTypes.getExtension("proba.").equals(""));
	assertTrue(FileTypes.getExtension("proba.doc").equals("doc"));
	assertTrue(FileTypes.getExtension("/proba.proba/proba.doc").equals("doc"));
    }
}
