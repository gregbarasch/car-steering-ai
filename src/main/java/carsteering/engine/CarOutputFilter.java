package carsteering.engine;

import carsteering.util.VectorMath;

/**
 * @author Greg Barasch
 */
public class CarOutputFilter {

    private double throttle = 0;
    private double brake = 0;
    private double steering = 0;

    public CarOutputFilter(Car subject, double[] accelerationVector, double delta_t) {

        double maxAcceleration = subject.getMaxVelocity() / delta_t;

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);

        // Use direction projection to set our linear acceleration
        if (directionProjection > 0) {
            this.throttle = Math.abs(directionProjection / maxAcceleration);
        } else if (directionProjection < 0) {
            this.brake = Math.abs(directionProjection / maxAcceleration);
        }

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ forwardVector[1], forwardVector[0] * -1 };
        double steerProjection = VectorMath.dotProduct(accelerationVector, rightVector);

        // Steering. Max acceleration is used as the unit for this as well.
        if (steerProjection != 0) {
            this.steering = steerProjection / maxAcceleration * -1;
        }
    }

    public double getThrottle() {
        return throttle;
    }

    public double getBrake() {
        return brake;
    }

    public double getSteering() {
        return steering;
    }
}
