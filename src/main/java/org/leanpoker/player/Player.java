package org.leanpoker.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        JsonObject json = request.getAsJsonObject();
        int minRaise = json.get("minimum_raise").getAsInt();
        int currentBuyIn = json.get("current_buy_in").getAsInt();

        //String[] json = json.getAsJsonArray()

        return minRaise+currentBuyIn;
    }

    public static void showdown(JsonElement game) {
    }
}
