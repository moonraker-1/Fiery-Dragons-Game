package fierydragons;


public class Player extends SpriteObject implements Savable {

    private final DragonToken token;

    public Player(DragonToken token) {
        this.token = token;
    }

    public DragonToken getToken() {
        return token;
    }

    @Override
    public String save() {

        return "1";
    }

    //    UNUSED
//    public void setToken(DragonToken token) {
//        this.token = token;
//    }


}
