package fierydragons;


public abstract class CreatureContainer extends SpriteObject {

    private CreatureType creature;

    public CreatureContainer(CreatureType creature) {
        this.creature = creature;
    }

    public CreatureType getCreature() {
        return creature;
    }


    public void setCreature(CreatureType creature) {
        this.creature = creature;
    }
}
