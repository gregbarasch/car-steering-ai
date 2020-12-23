package carsteering.test;

import carsteering.controllers.ArriveController;
import carsteering.engine.Car;
import carsteering.engine.Game;
import carsteering.engine.GameObject;
import carsteering.engine.GameWindow;
import carsteering.engine.Marker;
import carsteering.engine.Obstacle;
import java.awt.Color;

/**
 *
 * @author santi
 * @author Greg Barasch
 */
public class ArriveScenario {
    /*
        Goal of this exercise:
        - Write a controller for "car1" that uses the "Arrive" steering behavior to arrive to
          a given marker.
        - To make sure it works, carsteering.test your controller by placing the marker in different positions
          in the map.
    */

    public static void main(String[] args) throws Exception {
        Game game = new Game(800,600, 25);
        // set up the outside walls:
        game.add(new Obstacle(0,0,800,25,Color.GRAY));
        game.add(new Obstacle(0,575,800,25,Color.GRAY));
        game.add(new Obstacle(0,0,25,600,Color.GRAY));
        game.add(new Obstacle(775,0,25,600,Color.GRAY));
        // set up the cars and markers:
        GameObject marker = new Marker(600,300,10, Color.green);
        GameObject car1 = new Car("graphics/redcar.png",200,305,-Math.PI/2, new ArriveController(marker));
        game.add(marker);
        game.add(car1);
        GameWindow.newWindow(game);
    }
}
