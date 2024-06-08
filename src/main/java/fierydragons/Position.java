package fierydragons;


import java.util.List;

public abstract class Position extends CreatureContainer {

    private Boolean occupied = false;

    //private Integer id;

    public Position(CreatureType creature) {
        super(creature);
        //this.id = id;
    }

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

    public Boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    // Retrieves image coordinates
    public Double getX() {
        return this.getNodes().get(0).getLayoutX();
    }
    public Double getY() {
        return this.getNodes().get(0).getLayoutY();
    }


    // Retrieves connected positions
    public abstract List<Position> getNextPos();

    public abstract List<Position> getPrevPos();

}
