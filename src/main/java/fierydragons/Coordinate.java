package fierydragons;


// Class for converting polar coordinates to cartesian (pixel)
public class Coordinate {


    // Convert polar coordinates to cartesian (pixel) coordinates
    public static Double toX(Integer r, double angle) {
        return r * Math.cos(Math.toRadians(angle));
    }

    public static Double toY(Integer r, double angle) {
        return r * Math.sin(Math.toRadians(angle));
    }

}
