package Abrielle.util.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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

        InputStream is = getFile("config.json");
        StringBuilder in = new StringBuilder();

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                in.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        obj = parser.parse(in.toString());
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
