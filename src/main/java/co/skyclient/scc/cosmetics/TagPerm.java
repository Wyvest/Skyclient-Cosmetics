package co.skyclient.scc.cosmetics;

import cc.polyfrost.oneconfig.utils.NetworkUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class TagPerm {
    private final String identifier;
    private final ArrayList<String> users;

    TagPerm(JsonArray users, String identifier) {
        ArrayList<String> profiles = new ArrayList<>();
        for (JsonElement element : users) {
            String uuid = element.getAsString();
            try {
                String profile = getName(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20));
                if (profile != null) profiles.add(profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.users = profiles;
        this.identifier = identifier;
    }

    public static String getName(String uuid) {
        JsonObject object = NetworkUtils.getJsonElement("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).getAsJsonObject();
        return object.get("name").getAsString();
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "TagPerm(" +
                "identifier='" + identifier + '\'' +
                ", users=" + users +
                ')';
    }
}
