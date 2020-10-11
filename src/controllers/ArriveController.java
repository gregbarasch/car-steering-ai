package controllers;

import engine.Car;
import engine.CarOutputFilter;
import engine.Game;
import engine.GameObject;
import util.VectorMath;

/**
 * @author Greg Barasch
 */
public class ArriveController extends Controller {

    private final GameObject target;

    public ArriveController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Get acceleration vector
        double[] accelerationVector = arrive(subject, target);
        subject.setDebugDistanceVector(accelerationVector);

        CarOutputFilter carOutputFilter = new CarOutputFilter(subject, accelerationVector);
        controlVariables[VARIABLE_STEERING] = carOutputFilter.getSteering();
        controlVariables[VARIABLE_THROTTLE] = carOutputFilter.getThrottle();
        controlVariables[VARIABLE_BRAKE] = carOutputFilter.getBrake();
    }

    private double[] arrive(Car subject, GameObject target) {

        // Get our distance magnitude..
        double[] distance = VectorMath.distance(subject, target);
        double distanceMagnitude = VectorMath.magnitude(distance);

        // FIXME pass these in... idk
        double targetRadius = 5.0;
        double slowRadius = 200.0;

        // Are we close enough?
        if (distanceMagnitude < targetRadius) {
            return new double[] { 0, 0 };
        }

        // Compute our target speed
        double targetSpeed = subject.getMaxVelocity();
        if (distanceMagnitude <= slowRadius) {
            targetSpeed = subject.getMaxVelocity() * distanceMagnitude / slowRadius;
        }

        // Compute the target velocity
        double[] normalizedDistance = VectorMath.divide(distance, distanceMagnitude);
        double[] targetVelocity = VectorMath.multiply(normalizedDistance, targetSpeed);

        // Compute the acceleration vector
        double[] velocityVector = new double[]{
                Math.cos(subject.getAngle()) * subject.getSpeed(),
                Math.sin(subject.getAngle()) * subject.getSpeed()
        };
        double[] accelerationVector = VectorMath.subtract(targetVelocity, velocityVector);

        // Check if we exceeded our max acceleration
        if (VectorMath.magnitude(accelerationVector) > subject.getMaxVelocity()) {
            accelerationVector = VectorMath.multiply(VectorMath.normalize(accelerationVector), subject.getMaxVelocity());
        }

        return accelerationVector;
    }
}
