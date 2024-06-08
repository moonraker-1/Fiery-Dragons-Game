package fierydragons;


public class ChitCard extends CreatureContainer implements Savable
{
    private final Integer count;

    public ChitCard(CreatureType creature, Integer count) {
        super(creature);
        this.count = count;
    }


    public Integer getCount() {
        return count;
    }

    @Override
    public String save() {

        return this.getCreature() + "," + this.count;
    }

}
