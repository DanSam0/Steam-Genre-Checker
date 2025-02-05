package com.dansam0.steamdemo.steam;

import com.dansam0.steamdemo.dto.GameDto;
import com.dansam0.steamdemo.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Component
public class SteamDataProcessor {

    private final ObjectMapper objectMapper;

    public SteamDataProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public long readProfileId(InputStream inputStream){

        JsonNode jsonNode = null;

        try{
            jsonNode = objectMapper.readValue(inputStream, JsonNode.class);
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read JSON data", e);
        }

        JsonNode result = jsonNode.get("response");

        if(result.get("success").asInt() == 42)
            return 0;

        return result.get("steamid").asLong();
    }

    public Profile readProfile(InputStream inputStream){

        JsonNode jsonNode = null;

        try{
            jsonNode = objectMapper.readValue(inputStream, JsonNode.class);
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read JSON data", e);
        }

        JsonNode result = getProfiles(jsonNode);

        System.out.println(result);

        if (result.isNull())
            System.out.println("It's empty, nothing in there, pider");

        List<Profile> profilesList = new ArrayList<>();

        for (JsonNode profile : result) {

            if(profile.get("communityvisibilitystate").asInt() == 1)
                break;

            long id = Long.parseLong(profile.get("steamid").asText());
            String nickname = profile.get("personaname").asText();
            String avatar = profile.get("avatarfull").asText();
            Timestamp creationTime = Timestamp.from(Instant.ofEpochSecond(profile.get("timecreated").asLong()));

            Profile newProfile = new Profile();

            newProfile.setSteamId(id);
            newProfile.setName(nickname);
            newProfile.setAvatar(avatar);
            newProfile.setCreationTime(creationTime);

            profilesList.add(newProfile);
        }

        if(profilesList.isEmpty())
            return null;

        return profilesList.get(0);
    }

    public List<GameDto> readGames(InputStream inputStream){

        JsonNode jsonNode = null;

        try{
            jsonNode = objectMapper.readValue(inputStream, JsonNode.class);
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read JSON data", e);
        }

        JsonNode result = null;

        try{
            result = getGames(jsonNode);
        } catch (IllegalArgumentException e){
            return null;
        }

        List<GameDto> games = new ArrayList<>();

        for (JsonNode gameInfo : result) {

            GameDto tempGame = new GameDto();

            tempGame.setId(gameInfo.get("appid").asInt());
            tempGame.setPlaytime(gameInfo.get("playtime_forever").asInt());

            games.add(tempGame);
        }

        return games;
    }

    public List<Genre> readGenres(InputStream inputStream) throws IOException {

        JsonNode jsonNode = null;

        try{
            jsonNode = objectMapper.readValue(inputStream, JsonNode.class);
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read JSON data", e);
        }

        JsonNode result = jsonNode.get("tags");

        List<Genre> genreList = new ArrayList<>();

        Iterator<String> fieldNames = result.fieldNames();
        while (fieldNames.hasNext()) {

            Genre tempGenre = new Genre();
            tempGenre.setName(fieldNames.next());

            genreList.add(tempGenre);
        }

        return genreList;
    }

    private JsonNode getProfiles(JsonNode jsonNode) {

        return Optional.ofNullable(jsonNode)
                .map(j -> j.get("response"))
                .map(j -> j.get("players"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid JSON object"));
    }

    private JsonNode getGames(JsonNode jsonNode) {

        return Optional.ofNullable(jsonNode)
                .map(j -> j.get("response"))
                .map(j -> j.get("games"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid JSON object"));
    }


}
