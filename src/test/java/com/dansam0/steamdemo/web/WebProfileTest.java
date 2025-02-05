package com.dansam0.steamdemo.web;

import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class ProfileDtoGenre {

    @Test
    void setPlaytime() throws ParseException {

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String givenString = decimalFormat.format((float) 124521/60);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        decimalFormat = new DecimalFormat("#.0");
        decimalFormat.setDecimalFormatSymbols(symbols);

        Float result = decimalFormat.parse(givenString).floatValue();

        System.out.println(result);

        assertEquals(2075.4f, result);
    }
}