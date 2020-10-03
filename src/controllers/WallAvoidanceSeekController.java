package controllers;

import engine.Car;
import engine.Game;

public class WallAvoidanceSeekController extends Controller {

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;
    }

}
