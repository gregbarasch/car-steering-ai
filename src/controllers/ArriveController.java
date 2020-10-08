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

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);

        // Use direction projection to set our linear acceleration
        if (directionProjection > 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else if (directionProjection < 0) {
            controlVariables[VARIABLE_THROTTLE] = -1;
        } else {
            controlVariables[VARIABLE_THROTTLE] = 0;
        }
        controlVariables[VARIABLE_BRAKE] = 0;

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

    private double[] arrive(Car self, GameObject target) {
        // Get our distance magnitude..
        double[] distance = VectorMath.distance(self, target);
        double distanceMagnitude = VectorMath.magnitude(distance);

        double targetRadius = 5.0;
        double slowRadius = 350.0;

        // Are we close enough?
        if (distanceMagnitude < targetRadius) {
            return new double[] { 0, 0 };
        }
        System.out.println(distanceMagnitude);

        // Compute our target speed
        double maxSpeed = 250.0; // FIXME maxspeed
        double targetSpeed = maxSpeed;
        if (distanceMagnitude <= slowRadius) {
            targetSpeed = maxSpeed * distanceMagnitude / slowRadius;
        }

        // Compute the target velocity
        double[] normalizedDistance = VectorMath.divide(distance, distanceMagnitude);
        double[] targetVelocity = VectorMath.multiply(normalizedDistance, targetSpeed);

        // Compute the acceleration vector
        double[] velocityVector = new double[]{ Math.cos(self.getAngle()) * self.getSpeed(), Math.sin(self.getAngle()) * self.getSpeed() };
        double[] accelerationVector = VectorMath.subtract(targetVelocity, velocityVector); // FIXME divided by time?

        // Check if we exceeded our max acceleration // FIXME max acceleration
        if (VectorMath.magnitude(accelerationVector) > 250) {
            accelerationVector = VectorMath.multiply(VectorMath.normalize(accelerationVector), 250);
        }

        return accelerationVector;
    }
}
