package fierydragons;


import java.util.ArrayList;
import java.util.List;

public class VolcanoSquare extends Position implements Savable{

    private VolcanoSquare nextSquare;
    private VolcanoSquare prevSquare;

    private final VolcanoCard volcanoCard;

    // UNUSED
//    private Position prevPos;
//    private Position nextPos;

    public VolcanoSquare(CreatureType creature, VolcanoCard volcanoCard) {
        super(creature);
        this.volcanoCard = volcanoCard;
    }

    // UNUSED
//    public VolcanoSquare getNextSquare() {
//        return nextSquare;
//    }
//    public VolcanoSquare getPrevSquare() {
//        return prevSquare;
//    }
//
//    public VolcanoCard getVolcanoCard() {
//        return this.volcanoCard;
//    }

    public void setNextSquare(VolcanoSquare nextSquare) {
        this.nextSquare = nextSquare;
    }

    public void setPrevSquare(VolcanoSquare prevSquare) {
        this.prevSquare = prevSquare;
    }


    @Override
    public List<Position> getNextPos() {

        List<Position> positions = new ArrayList<>();
        positions.add(this.nextSquare);

        if (this.volcanoCard.isCut() && this == this.volcanoCard.getCaveSquare()) {
            positions.add(this.volcanoCard.getCaveJoin());
        }

        return positions;
    }

    @Override
    public List<Position> getPrevPos() {
        List<Position> positions = new ArrayList<>();
        positions.add(this.prevSquare);

        if (this.volcanoCard.isCut() && this == this.volcanoCard.getCenterSquare()) {
            positions.add(this.volcanoCard.getCaveJoin());
        }

        return positions;
    }


    @Override
    public String save() {

        return this.getCreature().toString();// + "," + this.getId();
    }

}
