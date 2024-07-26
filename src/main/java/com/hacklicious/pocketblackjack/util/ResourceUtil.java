package com.hacklicious.pocketblackjack.util;

import java.net.URL;

public class ResourceUtil {
    public static URL getResourceAsUrl(String resourceName) {
        return ResourceUtil.class.getClassLoader().getResource(resourceName);
    }
}
