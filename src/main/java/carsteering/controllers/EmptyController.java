package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.Game;

/**
 *
 * @author santi
 */
public class EmptyController extends Controller {

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;
    }

}
