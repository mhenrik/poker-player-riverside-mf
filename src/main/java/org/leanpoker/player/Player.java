package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class Player {

    static final String VERSION = "ALL IN";
    static boolean raised = false;
    static int currentRound;
    static Map<String, Integer> cardValue = new HashMap<String, Integer>(){{
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("6", 6);
        put("7", 7);
        put("8", 8);
        put("9", 9);
        put("10", 10);
        put("J", 11);
        put("Q", 12);
        put("K", 13);
        put("A", 14);
    }};

    public static int betRequest(JsonElement request) {


        System.out.println("TURN BEGINS");
        JsonObject json = request.getAsJsonObject();
        int minRaise = json.get("minimum_raise").getAsInt();
        int currentBuyIn = json.get("current_buy_in").getAsInt();
        JsonArray players = json.get("players").getAsJsonArray();
        int inAction = json.get("in_action").getAsInt();
        JsonObject ourPlayer = players.get(inAction).getAsJsonObject();
        int ourBet = ourPlayer.get("bet").getAsInt();
        int round = json.get("round").getAsInt();
        if(currentRound < round){
            raised = false;
            currentRound = round;
        }

        int maxStack = 0;
        for (JsonElement player : players) {
            JsonObject jsonPlayer = player.getAsJsonObject();
            int stack = jsonPlayer.get("stack").getAsInt();
            if (stack > maxStack){
                maxStack = stack;
            }
        }

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

        // get pot
        int pot = json.get("pot").getAsInt();

        // without community cards
        if(checkCommCards(json).size() == 0){

            if (highCards.contains(ourCards.get(0)) && highCards.contains(ourCards.get(1))) {
                if (ourCards.get(0).equals(ourCards.get(1))){
                    if (maxStack == ourPlayer.get("stack").getAsInt()){
                        return ourPlayer.get("stack").getAsInt();
                    }
                    if(raised){
                        return 0;
                    }
                    raised = true;
                    return currentBuyIn - ourBet + minRaise;

                }
                int toBet = currentBuyIn - ourBet + pot / 2;
                int currentStack = ourPlayer.get("stack").getAsInt();

                if (toBet > currentStack) {
                    return currentStack;
                }
                return toBet;

            }

            /*if (highCards.contains(ourCards.get(0)) || highCards.contains(ourCards.get(1))) {
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
*/
            if (ourCardsSUIT.get(0).equals(ourCardsSUIT.get(1))){
                if (highCards.contains(ourCards.get(0)) && highCards.contains(ourCards.get(1))){

                    return currentBuyIn - ourBet + minRaise;
                }
                return 0;


            }

            return 0;
        }

      //POSTFLOP

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

        if(checkCommCards(json).size() == 3){
            System.out.println(currentSuits.get(ourCardsSUIT.get(0)));

            //
            if(currentSuits.get(ourCardsSUIT.get(0)) >= 4 || currentSuits.get(ourCardsSUIT.get(1)) >= 4){
                return currentBuyIn - ourBet + minRaise;

            }//dasda

            int duplicateCard  = Collections.frequency(comCards, ourCards.get(0));
            int duplicateCard2 = Collections.frequency(comCards, ourCards.get(1));

            if(duplicateCard >= 2 || duplicateCard2 >= 2){
                return ourPlayer.get("stack").getAsInt();

            }

            for (String card : comCards) {
                if (ourCards.get(0).equals(ourCards.get(1)) && card.equals(ourCards.get(0))){
                    return ourPlayer.get("stack").getAsInt();
                }

            }
            for(String card : comCards){
                if (cardValue.get(card) > cardValue.get(ourCards.get(0)) && cardValue.get(card) > cardValue.get(ourCards.get(1))) {
                    return 0;
                }
                if(ourCards.get(0).equals(card) || ourCards.get(1).equals(card) || ourCards.get(0).equals(ourCards.get(1))){
                    return ourPlayer.get("stack").getAsInt();
                }
            }
        }
        if(checkCommCards(json).size() == 4 || checkCommCards(json).size() == 5) {
            if (currentSuits.get(ourCardsSUIT.get(0)) == 5 || currentSuits.get(ourCardsSUIT.get(1)) == 5) {
                return ourPlayer.get("stack").getAsInt();
            }

            int duplicateCard  = Collections.frequency(comCards, ourCards.get(0));
            int duplicateCard2 = Collections.frequency(comCards, ourCards.get(1));

            if(duplicateCard >= 2 || duplicateCard2 >= 2){
                return ourPlayer.get("stack").getAsInt();

            }

            for (String card : comCards) {
                if (ourCards.get(0).equals(ourCards.get(1)) && card.equals(ourCards.get(0))){
                    return ourPlayer.get("stack").getAsInt();
                }

            }


        }



        return 0;
    }

    public static boolean isTwoPair(List<String> allCards) {

        List<Boolean> boolList = new ArrayList<>();
        for (String card1 : allCards) {
            int counter = -1;
            for (String card2 : allCards) {
                if (card1.equals(card2)) {
                    counter++;
                }
            }
            if(counter == 2) {
                boolList.add(true);
            } else {
                boolList.add(false);
            }
        }

        int pairs = 0;
        for (Boolean bool : boolList) {
            if (bool) {
                pairs++;
            }
        }

        return pairs == 2;
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




/*    public static boolean straightChecker(List<String> ourcards, List<String> comcards){
        int cardvalue1 = cardValue.get(ourcards.get(0));
        int cardvalue2 = cardValue.get(ourcards.get(2));

        if (Math.abs(cardvalue1 - cardvalue2) < 2){

        }

    }*/



    public static void showdown(JsonElement game) {
    }
}
