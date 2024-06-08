package fierydragons;


import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class SpriteObject {

    private List<Node> nodes = new ArrayList<>();

    public List<Node> getNodes() { return nodes; }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

}
