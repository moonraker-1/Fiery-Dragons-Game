package fierydragons;


import java.util.ArrayList;
import java.util.List;

public class Cave extends Position implements Savable {

    private VolcanoCard volcanoJoin;
    private boolean occupied;  // Add this property "occupied"


    public Cave(CreatureType creature) {
        super(creature);
    }

    // UNUSED
//    public VolcanoCard getVolcanoJoin() {
//        return volcanoJoin;
//    }

    public void setVolcanoJoin(VolcanoCard volcanoJoin) {
        this.volcanoJoin = volcanoJoin;
    }

    @Override
    public List<Position> getNextPos() {
        List<Position> positions = new ArrayList<>();
        positions.add(this.volcanoJoin.getCaveSquare());
        return positions;
    }

    @Override
    public List<Position> getPrevPos() {
        List<Position> positions = new ArrayList<>();
        positions.add(this);
        return positions;
    }

    @Override
    public String save() {

        return this.getCreature().toString();// + "," + this.getId();
    }

}
