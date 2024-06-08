package fierydragons;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board implements Savable {
    private static Board board = null;

    private List<ChitCard> chitCardsArray = new ArrayList<ChitCard>();
    private List<VolcanoCard> cutVolcanoArray = new ArrayList<>();
    private List<VolcanoCard> uncutVolcanoArray = new ArrayList<>();
    private List<Cave> caveArray = new ArrayList<>();
    private List<DragonToken> tokens = new ArrayList<>();
    private List<Player> players = new ArrayList<>();

    private Integer maxSteps;
    private Integer maxPirateSteps;

    private Integer piratesPerStep;
    private Integer playerCount;

    private String[] creatures;
    private List<String> cutVolcanoCards = new ArrayList<>();
    private List<String> uncutVolcanoCards = new ArrayList<>();
    private List<String> chitCards = new ArrayList<>();
    private List<String> caves = new ArrayList<>();


    // Private constructor for Singleton Design Pattern
    private Board() {}

    // getInstance() method for Singleton Design Pattern
    public static Board getInstance()
    {
        if (board == null)
            board = new Board();

        return board;
    }


    public void reset() {

        chitCardsArray.clear();
        cutVolcanoArray.clear();
        uncutVolcanoArray.clear();
        caveArray.clear();
        tokens.clear();
        players.clear();
        cutVolcanoCards.clear();
        uncutVolcanoCards.clear();
        chitCards.clear();
        caves.clear();

    }

    // For initialising the variables instead of the constructor.
    public void initChitCards(Integer maxSteps, Integer piratesPerStep, Integer maxPirateSteps) {
        this.maxSteps = maxSteps;
        this.piratesPerStep = piratesPerStep;
        this.maxPirateSteps = maxPirateSteps;

    }

    public void initPlayers(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public void initCreatures(String[] creatures) {
        this.creatures = creatures;
    }

    public void addToVolcano(String volcanoCard, Boolean cut) {

        if (cut) {
            cutVolcanoCards.add(volcanoCard);
        }
        else {
            uncutVolcanoCards.add(volcanoCard);
        }
    }

    public void addToChitCards(String chitCard) {
        chitCards.add(chitCard);
    }

    public void addToCaves(String cave) {
        caves.add(cave);
    }


    // Sets up the board by creating all the game pieces
    public void setup(Boolean init) {

        createChitCards(init);

        createVolcano(init);

        if (init) {
            shuffle();
        }

        joinVolcano();


        createTokens();

        createPlayers();

        TurnManager.setCurrPlayerIndex(0); // Setting the first player
    }

    public void shuffle() {
        Collections.shuffle(chitCardsArray);
        Collections.shuffle(caveArray);
        Collections.shuffle(cutVolcanoArray);
        Collections.shuffle(uncutVolcanoArray);
    }

    // Creates the chit card objects and stores them
    public void createChitCards(Boolean init) {

        if (init) {
            // Creating non pirate chit cards
            for (int i = 1; i <= this.maxSteps; i++) {
                for (CreatureType creatureType : CreatureType.values()) {
                    if (creatureType == CreatureType.DRAGON_PIRATE) {
                        break;
                    }
                    ChitCard chitCard = new ChitCard(creatureType, i);
                    chitCardsArray.add(chitCard);
                }
            }

            // Creating the pirate chit cards (3 cards total)
            int pirateCount = 0;
            for (int i = 1; i <= this.maxPirateSteps && pirateCount < 3; i++) {
                for (int j = 1; j <= this.piratesPerStep && pirateCount < 3; j++) {
                    ChitCard chitCard = new ChitCard(CreatureType.DRAGON_PIRATE, i);
                    chitCardsArray.add(chitCard);
                    pirateCount++;
                }
            }

            // Creating Devil chit card
            ChitCard chitCard = new ChitCard(CreatureType.DEVIL, 1);
            chitCardsArray.add(chitCard);

        }

        else {
            Integer iterator = 0;

            // Handling uncut volcano cards
            while (iterator < chitCards.size()) {
                String chitCardConfig = chitCards.get(iterator);
                String[] chitCardConfigArray = chitCardConfig.split(",");
                ChitCard chitCard = new ChitCard(CreatureType.valueOf(chitCardConfigArray[0].toUpperCase()), Integer.parseInt(chitCardConfigArray[1]));
                this.chitCardsArray.add(chitCard);
                iterator++;
            }
        }



        //
    }

    // Creates the volcano objects and stores them
    public void createVolcano(Boolean init) {
        // Handling cut volcano cards
        Integer iterator = 0; // Start from 0
        String cutVolcanoCardConfig;
        String[] cutVolcanoCardConfigArray;

        //Integer id = 0;

        while (iterator < cutVolcanoCards.size()) {
            cutVolcanoCardConfig = cutVolcanoCards.get(iterator);
            cutVolcanoCardConfigArray = cutVolcanoCardConfig.split(",");

            VolcanoCard volcanoCard = new VolcanoCard(true,Integer.valueOf(cutVolcanoCardConfigArray[0]));

            // Creating volcano squares from the retrieved configurations
            for (int i = 1; i < cutVolcanoCardConfigArray.length; i++) { // String s : cutVolcanoCardConfigArray
                volcanoCard.addSquare(new VolcanoSquare(CreatureType.valueOf(cutVolcanoCardConfigArray[i].toUpperCase()), volcanoCard));
                //id++;
            }
            this.cutVolcanoArray.add(volcanoCard);
            iterator++;
        }

        Cave cave;

        if (init)  {
            // Creating caves for each non-pirate creature and storing them
            for (String creature : this.creatures) {
                if (CreatureType.valueOf(creature.toUpperCase()) != CreatureType.DRAGON_PIRATE) { // and Devil
                    cave = new Cave(CreatureType.valueOf(creature.toUpperCase()));
                    cave.setOccupied(true);
                    //id++;
                    this.caveArray.add(cave);
                }
            }
        }


        else {
            iterator = 0;

            // Handling uncut volcano cards
            while (iterator < caves.size()) {
                String caveConfig = caves.get(iterator);

                cave = new Cave(CreatureType.valueOf(caveConfig.toUpperCase()));
                //id++;
                this.caveArray.add(cave);
                iterator++;
            }
        }


        iterator = 0; // Reset iterator for uncut volcano cards

        // Handling uncut volcano cards
        while (iterator < uncutVolcanoCards.size()) {
            String uncutVolcanoCardConfig = uncutVolcanoCards.get(iterator);
            String[] uncutVolcanoCardConfigArray = uncutVolcanoCardConfig.split(",");

            VolcanoCard volcanoCard = new VolcanoCard(false, 0);

            for (int i = 1; i < uncutVolcanoCardConfigArray.length; i++) { // String s : uncutVolcanoCardConfigArray
                volcanoCard.addSquare(new VolcanoSquare(CreatureType.valueOf(uncutVolcanoCardConfigArray[i].toUpperCase()), volcanoCard));
                //id++;
            }
            this.uncutVolcanoArray.add(volcanoCard);

            iterator++;
        }
    }


    public void joinVolcano() {
        // Connecting each cut volcano card to its corresponding cave
        for (int i = 0; i < caveArray.size(); i++) {
            cutVolcanoArray.get(i).setCaveJoin(caveArray.get(i));
            caveArray.get(i).setVolcanoJoin(cutVolcanoArray.get(i));
        }

        // Connecting the cut and uncut volcano cards with each other
        for (int i = 0; i < uncutVolcanoArray.size(); i++) {
            if (i == 0) {
                cutVolcanoArray.get(i).setPrevJoin(uncutVolcanoArray.get(i));
                cutVolcanoArray.get(i).setNextJoin(uncutVolcanoArray.get(1));

                uncutVolcanoArray.get(i).setPrevJoin(cutVolcanoArray.get(uncutVolcanoArray.size() - 1));
                uncutVolcanoArray.get(i).setNextJoin(cutVolcanoArray.get(i));
            } else if (i < uncutVolcanoArray.size() - 1) {
                cutVolcanoArray.get(i).setPrevJoin(uncutVolcanoArray.get(i));
                cutVolcanoArray.get(i).setNextJoin(uncutVolcanoArray.get(i + 1));

                uncutVolcanoArray.get(i).setPrevJoin(cutVolcanoArray.get(i - 1));
                uncutVolcanoArray.get(i).setNextJoin(cutVolcanoArray.get(i));
            } else if (i == uncutVolcanoArray.size() - 1) {
                cutVolcanoArray.get(i).setPrevJoin(uncutVolcanoArray.get(i));
                cutVolcanoArray.get(i).setNextJoin(uncutVolcanoArray.get(0));

                uncutVolcanoArray.get(i).setPrevJoin(cutVolcanoArray.get(i - 1));
                uncutVolcanoArray.get(i).setNextJoin(cutVolcanoArray.get(i));
            }
        }



        // Connecting all the volcano squares to each other
        // Ensure all volcano cards have their joins set and call joinSquares only if prevJoin and nextJoin are set
        for (VolcanoCard volcanoCard : cutVolcanoArray) {
            if (volcanoCard.getPrevJoin() != null && volcanoCard.getNextJoin() != null) {
                volcanoCard.joinSquares();
            } else {
                System.err.println("Error: VolcanoCard in cutVolcanoArray has null joins!");
            }
        }

        for (VolcanoCard volcanoCard : uncutVolcanoArray) {
            if (volcanoCard.getPrevJoin() != null && volcanoCard.getNextJoin() != null) {
                volcanoCard.joinSquares();
            } else {
                System.err.println("Error: VolcanoCard in uncutVolcanoArray has null joins!");
            }
        }
    }

    // Creates the dragon token objects and stores them
    public void createTokens() {


        for (int i = 0; i < this.playerCount; i++) {
            DragonToken token = new DragonToken();
            this.tokens.add(token);
            Cave cave = caveArray.get(i); // Get the cave corresponding to the token
            cave.setOccupied(true); // Set the cave to occupied
            token.setStartingCave(caveArray.get(i));
        }
    }

    // Creates player objects and stores them
    public void createPlayers() {
        for (int i = 0; i < this.playerCount; i++) {
            Player player = new Player(this.tokens.get(i));
            this.players.add(player);
        }
    }


    public List<ChitCard> getChitCardsArray() {
        return chitCardsArray;
    }

    public List<VolcanoCard> getVolcanoArray(Boolean cut) {
        if ((cut)) {
            return this.cutVolcanoArray;
        }
        return this.uncutVolcanoArray;
    }

    public List<Cave> getCaveArray() {
        return caveArray;
    }

    public List<DragonToken> getTokens() {
        return tokens;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public Integer getMaxSteps() {
        return maxSteps;
    }

    public Integer getPiratesPerStep() {
        return piratesPerStep;
    }

    public Integer getMaxPirateSteps() {
        return maxPirateSteps;
    }

    // For resetting the chit cards when a turn ends
    public void resetChitCards() {
        for (ChitCard chitCard : this.chitCardsArray) {
            Display.resetChitCard(chitCard);

        }
    }


    @Override
    public String save() {

        StringBuilder str = new StringBuilder();

        str.append("maxSteps");
        str.append("=");
        str.append(maxSteps.toString());
        str.append("\n");

        str.append("maxPirateSteps");
        str.append("=");
        str.append(maxPirateSteps.toString());
        str.append("\n");

        str.append("piratesPerStep");
        str.append("=");
        str.append(piratesPerStep.toString());
        str.append("\n");

        // private Integer playerCount;

        str.append("chitCards");
        str.append("=");
        for (ChitCard chitCard : chitCardsArray) {
            str.append(chitCard.save());
            str.append(";");
        }
        str.append("\n");

        str.append("cutVolcanoCards");
        str.append("=");
        for (VolcanoCard volcanoCard : cutVolcanoArray) {
            str.append(volcanoCard.save());
            str.append(";");
        }
        str.append("\n");

        str.append("uncutVolcanoCards");
        str.append("=");
        for (VolcanoCard volcanoCard : uncutVolcanoArray) {
            str.append(volcanoCard.save());
            str.append(";");
        }
        str.append("\n");


        str.append("caves");
        str.append("=");
        for (Cave cave : caveArray) {
            str.append(cave.save());
            str.append(";");
        }
        str.append("\n");

        str.append("tokens");
        str.append("=");
        for (DragonToken token : tokens) {
            str.append(token.save());
            str.append(";");
        }
        str.append("\n");

        str.append("players");
        str.append("=");
        for (Player player : players) {
            str.append(player.save());
            str.append(";");
        }
        str.append("\n");

        return str.toString();


    }

}
