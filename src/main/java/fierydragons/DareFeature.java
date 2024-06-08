package fierydragons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// A class for managing a feature called Daring - when a player decides to try the luck and pick one
// out of three cards
public class DareFeature {

    // List of players who have used the feature in the game
    private static List<Integer> playersUsedFeature = new ArrayList<>(4);

    // A method for running a DareScreen; returns a picked ChitCard
    public static CompletableFuture<ChitCard> run(Position position){
        DareScreen ds = new DareScreen();
        return ds.callScreen(position);
    }

    // Check whether the player has already used his attempt
    public static boolean isDisabledFor(int index){
        return playersUsedFeature.contains(index);
    }

    // Disable the feature for the player who has already used it
    public static void disableFromPlayer(int index){
        if (!playersUsedFeature.contains(index)){
            playersUsedFeature.add(index);
        }
    }

    public static String save() {
        StringBuilder str = new StringBuilder();

        str.append("daredPlayers");
        str.append("=");

        if (playersUsedFeature.size() > 0) {
            for (Integer integer : playersUsedFeature) {
                str.append(integer);
                str.append(";");
            }
            str.append("\n");
        }
        else {
            str.append(-1);
        }



        return str.toString();
    }
}
