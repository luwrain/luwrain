
//LWR_API 1.0

package org.luwrain.core;

import java.util.*;

public interface I18n
{
    String staticStr(LangStatic id);
    String getStaticStr(String id);
    String hasSpecialNameOfChar(char ch);
    String getCommandTitle(String command);
    Object getStrings(String component);
    String getPastTimeBrief(Date date);
    String getNumberStr(int count, String entities);
    String getExceptionDescr(Exception e);
    Lang getActiveLang();
    Lang getLang(String langName);
}
