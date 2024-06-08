package fierydragons;


public class DragonToken extends SpriteObject implements Savable {

    private Position position;

    private Cave startingCave;

    private Integer forwardStepsTaken = 0;
    private Integer backwardStepsTaken = 0;

    public DragonToken() {}

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public Cave getStartingCave() {
        return this.startingCave;
    }

    public void setStartingCave(Cave startingCave) {
        this.startingCave = startingCave;
    }


    public Integer getForwardStepsTaken() {
        return this.forwardStepsTaken;
    }

    public Integer getBackwardStepsTaken() {
        return this.backwardStepsTaken;
    }



    public void addSteps(Integer steps) {
        if (steps > 0) {
            this.forwardStepsTaken += steps;
        }
        else {
            this.backwardStepsTaken += steps;
        }
    }

    @Override
    public String save() {
//        System.out.println("TOKEN SAVE");
//        System.out.println(this.stepsTaken);

        return this.forwardStepsTaken + "," + this.backwardStepsTaken;
        //return String.valueOf(this.getPosition());
    }


}
