package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        System.out.println("TURN BEGINS");
        JsonObject json = request.getAsJsonObject();
        int minRaise = json.get("minimum_raise").getAsInt();
        int currentBuyIn = json.get("current_buy_in").getAsInt();
        JsonArray players = json.get("players").getAsJsonArray();
        int inAction = json.get("in_action").getAsInt();
        JsonObject ourPlayer = players.get(inAction).getAsJsonObject();
        int ourBet = ourPlayer.get("bet").getAsInt();

        List<String> ourCards = checkOurCards(ourPlayer);
        List<String> comCards = checkCommCards(json);

        List<String> ourCardsSUIT = checkOurCardsSUIT(ourPlayer);
        List<String> comCardsSUIT = checkCommCardsSUIT(json);


        Set<String> highCards = new HashSet<>();
        highCards.add("A");
        highCards.add("K");
        highCards.add("Q");
        highCards.add("J");
        highCards.add("10");
        highCards.add("9");

        // get pot
        int pot = json.get("pot").getAsInt();


        for(String card : comCards){
            if(ourCards.get(0).equals(card) || ourCards.get(1).equals(card) || ourCards.get(0).equals(ourCards.get(1))){
                System.out.println("GOT PAIR AFTER FLOP");
                return ourPlayer.get("stack").getAsInt();
            }
        }
        
        // without community cards
        if(checkCommCards(json).size() == 0){

            if (highCards.contains(ourCards.get(0)) && highCards.contains(ourCards.get(1))) {
                int toBet = currentBuyIn - ourBet + pot / 2;
                int currentStack = ourPlayer.get("stack").getAsInt();

                if (toBet > currentStack) {
                    return currentStack;
                }
                return toBet;
            }

            if (highCards.contains(ourCards.get(0)) || highCards.contains(ourCards.get(1))) {
                int toBet = currentBuyIn - ourBet + pot / 3;
                int currentStack = ourPlayer.get("stack").getAsInt();

                if (pot/5 > currentStack){
                    return 0;
                }
                if (toBet > currentStack) {
                    return currentStack;
                }


                return toBet;
            }

            if (ourCardsSUIT.get(0).equals(ourCardsSUIT.get(1))){
                return currentBuyIn - ourBet + minRaise;

            }

            return currentBuyIn - ourBet;
        }

      //POSTFLOP
        if(checkCommCards(json).size() == 3){

        }
        Map<String, Integer> currentSuits = new HashMap<String, Integer>(){{
            put("spades", 0);
            put("hearts", 0);
            put("clubs", 0);
            put("diamonds", 0);
        }};

        currentSuits.put(ourCardsSUIT.get(0), (currentSuits.get(ourCardsSUIT.get(0))+1));
        currentSuits.put(ourCardsSUIT.get(1), (currentSuits.get(ourCardsSUIT.get(1))+1));

        for(String cardSuit : comCardsSUIT){
            currentSuits.put(cardSuit, (currentSuits.get(cardSuit)+1));
        }





        return 0;
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



    public static List<String> checkOurCardsSUIT(JsonObject ourPlayer){
        List<String> cardRanks = new ArrayList<>();
        JsonArray ourCards = ourPlayer.get("hole_cards").getAsJsonArray();
        JsonObject cardOne = ourCards.get(0).getAsJsonObject();
        JsonObject cardTwo = ourCards.get(1).getAsJsonObject();

        cardRanks.add(cardOne.get("suit").getAsString());
        cardRanks.add(cardTwo.get("suit").getAsString());

        return cardRanks;

    }

    public static List<String> checkCommCardsSUIT(JsonObject json){

        List<String> cardRanks = new ArrayList<>();
        JsonArray comCards = json.get("community_cards").getAsJsonArray();

        for(JsonElement card : comCards){
            JsonObject currentCard = card.getAsJsonObject();
            cardRanks.add(currentCard.get("suit").getAsString());
        }

        return cardRanks;
    }

    public static void showdown(JsonElement game) {
    }
}
