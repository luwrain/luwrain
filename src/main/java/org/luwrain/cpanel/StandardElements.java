
package org.luwrain.cpanel;

public class StandardElements
{
    static public final Element ROOT = new SimpleElement(null, SimpleElement.class.getName() + ":ROOT");
    static public final Element APPLICATIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":APPLICATIONS");
    static public final Element INPUT_OUTPUT = new SimpleElement(ROOT, SimpleElement.class.getName() + ":InputOutput");
    static public final Element KEYBOARD = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":KEYBOARD");
    static public final Element SOUND = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SOUNDS");
    static public final Element BRAILLE = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":BRAILLE");
    static public final Element SPEECH = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SPEECH");
    static public final Element NETWORK = new SimpleElement(ROOT, SimpleElement.class.getName() + ":NETWORD");
    static public final Element HARDWARE = new SimpleElement(ROOT, SimpleElement.class.getName() + ":HARDWARE");
    static public final Element UI = new SimpleElement(ROOT, SimpleElement.class.getName() + ":UI");
    static public final Element EXTENSIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":EXTENSIONS");
    static public final Element WORKERS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":WORKERS");
}
