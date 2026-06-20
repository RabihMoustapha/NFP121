package com.isae.medialibrary.util;

import java.util.logging.Logger;

public class LogUtil {
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}