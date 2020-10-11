package controllers;

import engine.Car;
import engine.CarOutputFilter;
import engine.Game;
import engine.GameObject;
import util.VectorMath;

/**
 * @author Greg Barasch
 */
public class SeekController extends Controller {

    private final GameObject target;

    public SeekController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {

        // Get acceleration vector
        double[] accelerationVector = seek(subject, target, delta_t);
        subject.setDebugDistanceVector(accelerationVector);

        // Filter our acceleration and set our control variables
        CarOutputFilter carOutputFilter = new CarOutputFilter(subject, accelerationVector, delta_t);
        controlVariables[VARIABLE_STEERING] = carOutputFilter.getSteering();
        controlVariables[VARIABLE_THROTTLE] = carOutputFilter.getThrottle();
        controlVariables[VARIABLE_BRAKE] = carOutputFilter.getBrake();
    }

    private double[] seek(Car subject, GameObject target, double delta_t) {
        double[] distance = VectorMath.distance(subject, target);
        double[] normalizedDistance = VectorMath.normalize(distance);
        return VectorMath.multiply(normalizedDistance, subject.getMaxVelocity()/delta_t);
    }
}
