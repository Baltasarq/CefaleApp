// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


public class AppInfo {
    private enum BuildType { PHYSICIAN, PATIENT };
    public static final String NAME = "CefaleaApp";
    public static final String VERSION = "v2.0 20231201";
    private static final BuildType BUILD = BuildType.PHYSICIAN;
    public static final String FULL_NAME = NAME + " " + VERSION + buildAppTypeSuffix();
    private static String buildAppTypeSuffix()
    {
        String toret = BUILD.toString().toLowerCase();
        int len = toret.length();

        return toret.substring( len - 5, len - 1 );
    }
}
