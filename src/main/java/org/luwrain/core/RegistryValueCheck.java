
package org.luwrain.core;

public class RegistryValueCheck
{
    private String logger = "";
    private Registry registry;

    public RegistryValueCheck(Registry registry, String logger)
    {
	this.registry = registry;
	this.logger = logger != null?logger:"";
	if (registry == null)
	    throw new NullPointerException("Registry object may not be null");
    }

    public int intAny(String path, int defaultValue)
    {
	return defaultValue;
    }

    public int intPositive(String path, int defaultValue)
    {
	return defaultValue;
    }

    public int intPositiveNotZero(String path, int defaultValue)
    {
	return defaultValue;
    }

    public int intRange(String path, int min, int max, int defaultValue)
    {
	return defaultValue;
    }

    public String strNotEmpty(String path, String defaultValue)
    {
    return defaultValue;
    }
}
