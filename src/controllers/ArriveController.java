package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import util.VectorMath;

public class ArriveController extends Controller {

    private final GameObject target;

    public ArriveController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Get acceleration vector
        double[] accelerationVector = arrive(subject, target);
        subject.setDebugVector(accelerationVector);

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);
        System.out.println(directionProjection);
        double linearAcceleration = Math.abs(directionProjection) / (subject.getMaxVelocity()/delta_t);

        // Use direction projection to set our linear acceleration
        if (directionProjection > 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
            controlVariables[VARIABLE_BRAKE] = 0;
        } else if (directionProjection < 0) {
            controlVariables[VARIABLE_THROTTLE] = 0;
            controlVariables[VARIABLE_BRAKE] = 1;
        } else {
            controlVariables[VARIABLE_THROTTLE] = 0;
            controlVariables[VARIABLE_BRAKE] = 0;
        }

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ forwardVector[1], forwardVector[0]*-1 };
        double steerProjection = VectorMath.dotProduct(accelerationVector, rightVector);

        // Steering
        if (steerProjection > 0) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else if (steerProjection < 0) {
            controlVariables[VARIABLE_STEERING] = 1;
        } else {
            controlVariables[VARIABLE_STEERING] = 0;
        }
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
