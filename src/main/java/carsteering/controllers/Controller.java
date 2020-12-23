package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.Game;

/**
 *
 * @author santi
 * @author Greg Barasch
 */
public abstract class Controller {
    /*
        commands is an array with three components:
        - the desired "STEER" (-1 to +1)
        - the desired "THROTTLE" (0 to +1)
        - the desired "BRAKE" (0 to +1)
    */
    public static final int VARIABLE_STEERING = 0;
    public static final int VARIABLE_THROTTLE = 1;
    public static final int VARIABLE_BRAKE = 2;

    public abstract void update(Car subject, Game game, double delta_t, double[] controlVariables);
}
