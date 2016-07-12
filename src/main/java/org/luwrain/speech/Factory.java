
package org.luwrain.speech;

import org.luwrain.cpanel.Section;
import org.luwrain.cpanel.Element;

public interface Factory
{
    String getServedChannelType();
    Channel newChannel();
Section     newSettingsSection(Element el, String registryPath);
}
