package com.glivion.backend.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class Converters {

    public static String convertCentToActual(int cents){

        Locale usa = new Locale("en", "US");
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);

        return dollarFormat.format((cents / 100D));
    }

}
