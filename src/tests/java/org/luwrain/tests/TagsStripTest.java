
package org.luwrain.tests; 

import org.junit.*;

import org.luwrain.util.MlTagStrip;

public class TagsStripTest extends Assert
{
    @Test public void simple()
    {
	assertTrue(MlTagStrip.run("test").equals("test"));
	assertTrue(MlTagStrip.run("<b>test</b>").equals("test"));
	assertTrue(MlTagStrip.run("<p>test</p>").equals("test\n"));
	assertTrue(MlTagStrip.run("<P>test</P>").equals("test\n"));
	assertTrue(MlTagStrip.run("123<image>456").equals("123456"));
	assertTrue(MlTagStrip.run("123<image/>456").equals("123456"));
	assertTrue(MlTagStrip.run("123<image src=\"abc\"/>456").equals("123456"));
    }


    @Test public void cdata()
    {
	assertTrue(MlTagStrip.run("abc<![cdata[123]]>abc").equals("abc123abc"));
	assertTrue(MlTagStrip.run("abc<![cdata[<p>]]>abc").equals("abc<p>abc"));
	assertTrue(MlTagStrip.run("abc<![cdata[ <p> ]]>abc").equals("abc <p> abc"));
	assertTrue(MlTagStrip.run("abc<![cDaTa[ <p> ]]>abc").equals("abc <p> abc"));
	assertTrue(MlTagStrip.run("abc< ! [ c d a t a [ <p> ] ] >abc").equals("abc <p> abc"));
	assertTrue(MlTagStrip.run("abc<![cdata[123").equals("abc123"));
    }

    @Test public void entities()
    {
	assertTrue(MlTagStrip.run("&lt;").equals("<"));
	assertTrue(MlTagStrip.run("&gt;").equals(">"));
	assertTrue(MlTagStrip.run("&lt;p&gt;").equals("<p>"));

	assertTrue(MlTagStrip.run("Please say &quot;Hello!&quot;").equals("Please say \"Hello!\""));
	assertTrue(MlTagStrip.run("<span>&#38; &#38; &#38;</b>").equals("& & &"));
    }
}
