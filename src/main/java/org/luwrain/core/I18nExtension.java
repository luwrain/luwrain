
package org.luwrain.core;

public interface I18nExtension
{
    void addCommandTitle(String lang, String command, String title);
    void addStrings(String lang, String component, Object obj);
    boolean addLang(String name, Lang lang);
}
