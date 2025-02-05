package com.dansam0.steamdemo.dto;

import lombok.Data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

@Data
public class ProfileGenreDto {

    private String genreName;
    private float playtime;

    public void setPlaytime(Integer time) {

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String givenString = decimalFormat.format((float) time/60);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        decimalFormat.applyPattern("#.0");
        decimalFormat.setDecimalFormatSymbols(symbols);

        try {
            playtime = decimalFormat.parse(givenString).floatValue();
        } catch (ParseException e){
            throw new RuntimeException("Couldn't parse given string. " + e);
        }
    }

}
