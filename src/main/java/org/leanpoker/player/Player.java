package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        JsonObject json = request.getAsJsonObject();
        int minRaise = json.get("minimum_raise").getAsInt();
        int currentBuyIn = json.get("current_buy_in").getAsInt();
        JsonArray players = json.get("players").getAsJsonArray();
        int inAction = json.get("in_action").getAsInt();
        JsonObject ourPlayer = players.get(inAction).getAsJsonObject();
        int ourBet = ourPlayer.get("bet").getAsInt();

        List<String> ourCards = checkOurCards(ourPlayer);
        List<String> comCards = checkCommCards(json);

        for(String card : comCards){
            if(ourCards.get(0).equals(card) || ourCards.get(1).equals(card)){
                return ourPlayer.get("stack").getAsInt();
            }
        }


        return currentBuyIn - ourBet + minRaise;
    }

    public static void showdown(JsonElement game) {
    }

    public static List<String> checkOurCards(JsonObject ourPlayer){
        List<String> cardRanks = new ArrayList<>();
        JsonArray ourCards = ourPlayer.get("hole_cards").getAsJsonArray();
        JsonObject cardOne = ourCards.get(0).getAsJsonObject();
        JsonObject cardTwo = ourCards.get(1).getAsJsonObject();

        cardRanks.add(cardOne.get("rank").getAsString());
        cardRanks.add(cardTwo.get("rank").getAsString());

        return cardRanks;

    }

    public static List<String> checkCommCards(JsonObject json){

        List<String> cardRanks = new ArrayList<>();
        JsonArray comCards = json.get("community_cards").getAsJsonArray();

        for(JsonElement card : comCards){
            JsonObject currentCard = card.getAsJsonObject();
            cardRanks.add(currentCard.get("rank").getAsString());
        }

        return cardRanks;
    }

}
