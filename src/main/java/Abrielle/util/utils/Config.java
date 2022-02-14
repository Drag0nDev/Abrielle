package Abrielle.util.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

import static Abrielle.util.utils.Utils.getFile;

public class Config {
    private final String token;
    private final String owner;
    private final List<String> prefix;

    public Config() throws IOException, ParseException, URISyntaxException {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject config;

        obj = parser.parse(new FileReader(getFile("config.json")));
        config = (JSONObject) obj;

        this.token = (String) config.get("token");
        this.owner = (String) config.get("owner");

        JSONArray prefix = (JSONArray) config.get("prefix");
        this.prefix = (List<String>) prefix;
    }

    public String getToken() {
        return token;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getPrefix() {
        return prefix;
    }

}
