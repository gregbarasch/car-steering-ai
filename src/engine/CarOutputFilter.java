package engine;

import util.VectorMath;

public class CarOutputFilter {

    private double throttle = 0;
    private double brake = 0;
    private double steering = 0;

    public CarOutputFilter(Car subject, double[] accelerationVector) {

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);

        // Use direction projection to set our linear acceleration
        if (directionProjection > 0) {
            this.throttle = 1;
        } else if (directionProjection < 0) {
            this.brake = 1;
        }

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ forwardVector[1], forwardVector[0] * -1 };
        double steerProjection = VectorMath.dotProduct(accelerationVector, rightVector);

        // Steering
        if (steerProjection > 0) {
            this.steering = -1;
        } else if (steerProjection < 0) {
            this.steering = 1;
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
