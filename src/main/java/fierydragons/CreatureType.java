
package fierydragons;


public enum CreatureType {
    BABY_DRAGON("Baby_Dragon"),

    BAT("Bat"),

    SPIDER("Spider"),

    SALAMANDER("Salamander"),

    DRAGON_PIRATE("Dragon_Pirate"),

    // Create a creature called Devil
    DEVIL("Devil");

    private final String label;


    CreatureType(String label){
        this.label = label;
    }

    public String getSpritePath() {
        return Config.getConfig("game." + label.toLowerCase() + "Sprite");
    }

    /**
     *
     * @return the label text
     */
    @Override
    public String toString() {
        return label;
    }
}
