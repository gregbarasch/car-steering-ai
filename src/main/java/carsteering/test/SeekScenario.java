package carsteering.test;

import carsteering.controllers.KeyboardController;
import carsteering.controllers.SeekController;
import carsteering.engine.Car;
import carsteering.engine.Game;
import carsteering.engine.GameObject;
import carsteering.engine.GameWindow;
import carsteering.engine.Obstacle;
import java.awt.Color;

/**
 *
 * @author santi
 * @author Greg Barasch
 */
public class SeekScenario {
    /*
        Goal of this exercise:
        - Write a controller for "car2" that uses the "Seek" steerig behavior to always run
          after car1 (that is controlled using the arrow keys).
    */

    public static void main(String[] args) throws Exception {
        Game game = new Game(800,600, 25);
        // set up the outside walls:
        game.add(new Obstacle(0,0,800,25,Color.GRAY));
        game.add(new Obstacle(0,575,800,25,Color.GRAY));
        game.add(new Obstacle(0,0,25,600,Color.GRAY));
        game.add(new Obstacle(775,0,25,600,Color.GRAY));
        // set up the cars and markers:
        GameObject car1 = new Car("graphics/redcar.png",200,300,-Math.PI/2, new KeyboardController());
        GameObject car2 = new Car("graphics/bluecar.png",600,305,-Math.PI/2, new SeekController(car1));
        game.add(car1);
        game.add(car2);
        GameWindow.newWindow(game);
    }
}
