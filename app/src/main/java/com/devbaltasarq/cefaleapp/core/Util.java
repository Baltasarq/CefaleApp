// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import java.util.Calendar;
import java.util.Locale;

public class Util {
    /** @return a serial string, based on the ISO-8601 form: YYYY-MM-DD. */
    public static String buildSerial()
    {
        final Calendar TODAY = Calendar.getInstance();

        return String.format( Locale.getDefault(), "%04d%02d%02d%02d%02d%02d%03d",
                TODAY.get( Calendar.YEAR ),
                TODAY.get( Calendar.MONTH ),
                TODAY.get( Calendar.DAY_OF_MONTH ),
                TODAY.get( Calendar.HOUR ),
                TODAY.get( Calendar.MINUTE ),
                TODAY.get( Calendar.SECOND ),
                TODAY.get( Calendar.MILLISECOND ));
    }
}
