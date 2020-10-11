package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.CarOutputFilter;
import carsteering.engine.Game;
import carsteering.engine.GameObject;
import carsteering.util.VectorMath;

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
        double[] accelerationVector = arrive(subject, target, 210, 4, delta_t);
        subject.setDebugDistanceVector(accelerationVector);

        CarOutputFilter carOutputFilter = new CarOutputFilter(subject, accelerationVector, delta_t);
        controlVariables[VARIABLE_STEERING] = carOutputFilter.getSteering();
        controlVariables[VARIABLE_THROTTLE] = carOutputFilter.getThrottle();
        controlVariables[VARIABLE_BRAKE] = carOutputFilter.getBrake();
    }

    private double[] arrive(Car subject, GameObject target, double slowRadius, double targetRadius, double delta_t) {

        // Get our distance magnitude..
        double[] distance = VectorMath.distance(subject, target);
        double distanceMagnitude = VectorMath.magnitude(distance);

        // Are we close enough?
        if (distanceMagnitude <= targetRadius) {
            return new double[] { 0, 0 };
        }

        // Compute our target speed
        double targetSpeed = subject.getMaxVelocity();
        if (distanceMagnitude <= slowRadius) {
            targetSpeed = Math.abs(subject.getMinVelocity()) * distanceMagnitude / slowRadius;
        }

        // Compute the target velocity
        double[] normalizedDistance = VectorMath.divide(distance, distanceMagnitude);
        double[] targetVelocity = VectorMath.multiply(normalizedDistance, targetSpeed);

        // Compute the acceleration vector
        double[] velocityVector = new double[]{
                Math.cos(subject.getAngle()) * subject.getSpeed(),
                Math.sin(subject.getAngle()) * subject.getSpeed()
        };
        double[] accelerationVector = VectorMath.divide(VectorMath.subtract(targetVelocity, velocityVector), delta_t);

        // Check if we exceeded our max acceleration
        double maxAcceleration = subject.getMaxVelocity()/delta_t;
        if (VectorMath.magnitude(accelerationVector) > maxAcceleration) {
            accelerationVector = VectorMath.multiply(VectorMath.normalize(accelerationVector), maxAcceleration);
        }

        return accelerationVector;
    }
}
