
package org.luwrain.settings;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class BrailleSection extends SimpleFormSection
{
    BrailleSection()
    {
	super(StandardElements.BRAILLE, "Брайль");
	addString("111", 
		  (name)->{return "222";},
(name, value)->{});

    }
}
