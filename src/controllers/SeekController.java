package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import util.VectorMath;

public class SeekController extends Controller {

    private final GameObject target;

    public SeekController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Get acceleration vector
        double[] accelerationVector = seek(subject, target);

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);

        // Use direction projection to set our linear acceleration
        if (directionProjection < 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else {
            controlVariables[VARIABLE_THROTTLE] = -1;
        }
        controlVariables[VARIABLE_BRAKE] = 0;

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ forwardVector[1], forwardVector[0]*-1 };
        double steerProjection = VectorMath.dotProduct(accelerationVector, rightVector);

        // Steering
        if (steerProjection < 0) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else {
            controlVariables[VARIABLE_STEERING] = 1;
        }
    }

    private double[] seek(Car self, GameObject target) {
        double[] distance = VectorMath.distance(self, target);
        double[] normalizedDistance = VectorMath.normalize(distance);
        return VectorMath.multiply(normalizedDistance, 250); // FIXME max acceleration? hardcoded is fine for now
    }
}
