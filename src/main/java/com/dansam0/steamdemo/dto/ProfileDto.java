package com.dansam0.steamdemo.dto;

import com.dansam0.steamdemo.entity.Profile;
import lombok.Data;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

@Data
public class ProfileDto {

    private long steamId;
    private String name;
    private String avatar;
    private Timestamp lastUpdateTime;
    private Timestamp creationTime;
    private float overallPlaytime;

    public void setOverallPlaytime(Integer time){

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String givenString = decimalFormat.format((float) time/60);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        decimalFormat.applyPattern("#.0");
        decimalFormat.setDecimalFormatSymbols(symbols);

        try {
            overallPlaytime = decimalFormat.parse(givenString).floatValue();
        } catch (ParseException e){
            throw new RuntimeException("Couldn't parse given string. " + e);
        }
    }

    public void mapFromProfile(Profile profile){

        steamId = profile.getSteamId();
        name = profile.getName();
        avatar = profile.getAvatar();
        lastUpdateTime = profile.getLastUpdateTime();
        creationTime = profile.getCreationTime();
        setOverallPlaytime(profile.getOverallPlaytime());
    }

}
