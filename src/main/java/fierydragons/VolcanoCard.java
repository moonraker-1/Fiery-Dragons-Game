package fierydragons;


import java.util.ArrayList;
import java.util.List;

public class VolcanoCard implements Savable {

    private List<VolcanoSquare> squares = new ArrayList<>();

    private Integer caveSquareIndex; // for moving the cave

    private final Boolean cut;

    private VolcanoCard nextJoin;

    private VolcanoCard prevJoin;
    private Cave caveJoin;

    public VolcanoCard(Boolean cut, Integer caveSquareIndex) {
        this.cut = cut;
        this.caveSquareIndex = caveSquareIndex;
    }

    public void addSquare(VolcanoSquare square) {
        this.squares.add(square);
    }

    public Boolean isCut() {
        return cut;
    }

    public VolcanoCard getNextJoin() {
        return nextJoin;
    }

    public void setNextJoin(VolcanoCard nextJoin) {
        this.nextJoin = nextJoin;
    }

    public VolcanoCard getPrevJoin() {
        return prevJoin;
    }

    public void setPrevJoin(VolcanoCard prevJoin) {
        this.prevJoin = prevJoin;
    }

    public Cave getCaveJoin() {
        return caveJoin;
    }

    public void setCaveJoin(Cave caveJoin) {
        this.caveJoin = caveJoin;
    }

    public VolcanoSquare getSquare(Integer index) {
        return squares.get(index);
    }

    // UNUSED
//    public void setSquare(Integer index, VolcanoSquare square) {
//        squares.set(index, square);
//    }

    public List<VolcanoSquare> getSquares() {
        return squares;
    }

    // UNUSED
//    public void setSquares(List<VolcanoSquare> squares) {
//        this.squares = squares;
//    }


    // Joins the squares composing the volcano card to each other and to connecting ones on neighbouring volcano cards
    public void joinSquares() {

        for (int i = 0; i < squares.size(); i++) {
            if (i == 0) {
                squares.get(i).setNextSquare(squares.get(1));
                squares.get(i).setPrevSquare(this.getPrevJoin().getSquares().get(squares.size() - 1));

            } else if (i == squares.size() - 1) {
                squares.get(i).setNextSquare(this.getNextJoin().getSquares().get(0));
                squares.get(i).setPrevSquare(squares.get(i - 1));

            } else {
                squares.get(i).setNextSquare(squares.get(i + 1));
                squares.get(i).setPrevSquare(squares.get(i - 1));
            }

        }
    }


    public VolcanoSquare getCaveSquare() {
        return squares.get(caveSquareIndex);
    }

    public Integer getCaveSquareIndex() {
        return caveSquareIndex;
    }

    public void setCaveSquareIndex(Integer caveSquareIndex) {
        this.caveSquareIndex = caveSquareIndex;
    }

    @Override
    public String save() {
        StringBuilder str = new StringBuilder();
        str.append(caveSquareIndex);

        for (VolcanoSquare square : squares) {
            str.append(",");
            str.append(square.save());

        }

        return str.toString();
    }
    public VolcanoSquare getCenterSquare() {
        return squares.get(squares.size()/2);
    }

}
