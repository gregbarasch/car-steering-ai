package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import util.VectorMath;

public class SeekController extends Controller {

    private final Car target;

    public SeekController(Car target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {

        double[] accelerationVector = seek(subject, target);

        // velocity vector
        double vx = Math.cos(subject.getAngle())*subject.getSpeed()*delta_t;
        double vy = Math.sin(subject.getAngle())*subject.getSpeed()*delta_t;
        double[] velocityVector = { vx, vy };

        // Forward/Backward
        controlVariables[VARIABLE_BRAKE] = 0;


        double[] normalizedVelocityVector = VectorMath.normalize(velocityVector);
        double directionProjection = VectorMath.dotProduct(accelerationVector, normalizedVelocityVector);

        // Acceleration + set reverse steering while moving backwards
        boolean reverse = false;
        if (directionProjection > 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else if (directionProjection < 0) {
            controlVariables[VARIABLE_THROTTLE] = -1;
            reverse = true;
        } else {
            controlVariables[VARIABLE_THROTTLE] = 1;
        }

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ velocityVector[1], velocityVector[0]*-1 };
        double steerProjection = VectorMath.dotProduct(rightVector, accelerationVector);

        // TODO nonbinary steering? use the double?
        // Steering
        if (steerProjection > 0) {
            controlVariables[VARIABLE_STEERING] = 1;
        } else if (steerProjection < 0) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else {
            controlVariables[VARIABLE_STEERING] = 0;
        }
        // Steering is reversed when moving backwards
        if (reverse) controlVariables[VARIABLE_STEERING] *= -1;
    }

    private double[] seek(Car self, GameObject target) {
        double[] distance = VectorMath.distance(self, target);
        double[] normalizedDistance = VectorMath.divide(distance, VectorMath.absolute(distance));
        return VectorMath.multiply(normalizedDistance, 5); // FIXME max acceleration?
    }

}
