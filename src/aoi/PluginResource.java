package aoi;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class PluginResource {
    private String type, id;
    private ArrayList<String> names;
    private ArrayList<ClassLoader> loaders;
    private ArrayList<Locale> locales;

    private PluginResource(String type, String id)
    {
        this.type = type;
        this.id = id;
        names = new ArrayList<String>();
        loaders = new ArrayList<ClassLoader>();
        locales = new ArrayList<Locale>();
    }

    private void addResource(String name, ClassLoader loader, Locale locale) throws IllegalArgumentException
    {
        if (locales.contains(locale))
            throw new IllegalArgumentException("Multiple resource definitions for type="+type+", name="+ id +", locale="+locale);
        names.add(name);
        loaders.add(loader);
        locales.add(locale);
    }

    /**
     * Get the type of this PluginResource.
     */

    public String getType()
    {
        return type;
    }

    /**
     * Get the id of this PluginResource.
     */

    public String getId()
    {
        return id;
    }

    /**
     * Find which localized version of the resource best matches a locale.
     */

    private int findLocalizedVersion(Locale locale)
    {
        int bestMatch = 0, bestMatchedLevels = 0;
        for (int i = 0; i < locales.size(); i++)
        {
            Locale loc = locales.get(i);
            int matchedLevels = 0;
            if (loc != null && loc.getLanguage() == locale.getLanguage())
            {
                matchedLevels++;
                if (loc.getCountry() == locale.getCountry())
                {
                    matchedLevels++;
                    if (loc.getVariant() == locale.getVariant())
                        matchedLevels++;
                }
            }
            if (matchedLevels > bestMatchedLevels)
            {
                bestMatch = i;
                bestMatchedLevels = matchedLevels;
            }
        }
        return bestMatch;
    }

    /**
     * Get an InputStream for reading this resource.  If there are multiple localized versions,
     * the version which best matches the currently selected locale is used.
     */

    public InputStream getInputStream()
    {
        int index = findLocalizedVersion(Translate.getLocale());
        return loaders.get(index).getResourceAsStream(names.get(index));
    }

    /**
     * Get a URL for reading this resource.  If there are multiple localized versions,
     * the version which best matches the currently selected locale is used.
     */

    public URL getURL()
    {
        int index = findLocalizedVersion(Translate.getLocale());
        return (loaders.get(index)).getResource(names.get(index));
    }

    /**
     * Get the fully qualified name of the resource this represents.  If there are multiple localized
     * versions, the version which best matches the currently selected locale is used.
     */

    public String getName()
    {
        int index = findLocalizedVersion(Translate.getLocale());
        return names.get(index);
    }

    /**
     * Get the ClassLoader responsible for loading this resource.  If there are multiple localized
     * versions, the version which best matches the currently selected locale is used.
     */

    public ClassLoader getClassLoader()
    {
        int index = findLocalizedVersion(Translate.getLocale());
        return loaders.get(index);
    }
}