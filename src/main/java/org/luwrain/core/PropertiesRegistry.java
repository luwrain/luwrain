
package org.luwrain.core;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.luwrain.base.*;

final class PropertiesRegistry implements PropertiesBase
{
    private final PropertiesProvider[] basicProviders;
    private Provider[] providers = new Provider[0];
    private final Map<String, Provider> propsCache = new HashMap();
        private final Map<String, Provider> filesPropsCache = new HashMap();

    PropertiesRegistry(PropertiesProvider[] basicProviders)
    {
	NullCheck.notNullItems(basicProviders, "basicProviders");
	this.basicProviders = basicProviders;
	setProviders(new PropertiesProvider[0]);//to apply the list of basic providers
    }

    public PropertiesProvider[] getBasicProviders()
    {
	return basicProviders.clone();
    }

    /**
     * Sets new list of providers.
     *
     * @param providers The new providers
     * @return True if all providers are valid and the current list was updated, FALSE otherwise
     */
    boolean setProviders(PropertiesProvider[] providers)
    {
	NullCheck.notNullItems(providers, "providers");
	final List<Provider> newProviders = new LinkedList();
	for(PropertiesProvider p: basicProviders)
	    newProviders.add(new Provider(true, p));
		for(PropertiesProvider p: providers)
		    newProviders.add(new Provider(false, p));
	this.providers = newProviders.toArray(new Provider[newProviders.size()]);
	propsCache.clear();
	filesPropsCache.clear();
	return true;
    }

    /**
     * Returns a value of the property.
     *
     * @param propName A name of the property, may not be empty
     * @returns A value of the property or {@code null}, if there is no such property
     */
    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	if (propsCache.containsKey(propName))
	{
	    final String value = propsCache.get(propName).provider.getProperty(propName);
	    if (value != null)
		return value;
	    propsCache.remove(propName);
	}
	for(Provider p: providers)
	    if (p.basic && p.hasResponsibilitySpace() && p.matches(propName))
	    {
		final String value = p.provider.getProperty(propName);
		if (value != null)
		{
		    propsCache.put(propName, p);
		    return value;
		}
	    }
	for(Provider p: providers)
	    if (p.basic && !p.hasResponsibilitySpace())
	    {
		final String value = p.provider.getProperty(propName);
		if (value != null)
		{
		    propsCache.put(propName, p);
		    return value;
		}
	    }
	for(Provider p: providers)
	    if (!p.basic && p.hasResponsibilitySpace() && p.matches(propName))
	    {
		final String value = p.provider.getProperty(propName);
		if (value != null)
		{
		    propsCache.put(propName, p);
		    return value;
		}
	    }

				for(Provider p: providers)
		    if (!p.basic && !p.hasResponsibilitySpace())
		    {
			final String value = p.provider.getProperty(propName);
			if (value != null)
			{
			    propsCache.put(propName, p);
			    return value;
			}
		    }


		
	return "";
	}

    @Override public File getFileProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");

			for(Provider p: providers)
		    if (!p.hasResponsibilitySpace())
		    {
			final String value = p.provider.getProperty(propName);
			if (value != null && !value.isEmpty())
			    return new File(value);
		    }
	return null;
    }

    static private final class Provider
    {
	final boolean basic;
	final PropertiesProvider provider;
	final Pattern[] patterns;

	Provider(boolean basic, PropertiesProvider provider)
	{
	    NullCheck.notNull(provider, "provider");
	    this.basic = basic;
	    this.provider = provider;
	    final String[] regex = provider.getPropertiesRegex();
	    NullCheck.notNullItems(regex, "regex");
	    final List<Pattern> patterns = new LinkedList();
	    for (String r: regex)
		if (!r.isEmpty())
		    patterns.add(Pattern.compile(r));//Pattern.CASE_INSENSITIVE	    this.patterns = patterns.toArray(new Pattern[patterns.size()]);
	    this.patterns = patterns.toArray(new Pattern[patterns.size()]);
	}

	boolean hasResponsibilitySpace()
	{
	    return patterns.length > 0;
	}

	boolean matches(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    for(Pattern p: patterns)
	    {
	    	final Matcher matcher = p.matcher(propName);
		if (matcher.find())
		    return true;
	    }
	    return false;
	}
    }
}
