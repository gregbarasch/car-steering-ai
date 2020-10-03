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

        double[] acceleration = seek(subject, target);

        // velocity vector
        double vx = Math.cos(subject.getAngle())*subject.getSpeed()*delta_t;
        double vy = Math.sin(subject.getAngle())*subject.getSpeed()*delta_t;
        double[] velocityVector = { vx, vy };

        // Steering
        double[] rightVector = new double[]{ velocityVector[1], velocityVector[0]*-1 };
        double rightVectorAProjection = VectorMath.dotProduct(rightVector, acceleration);

        if (rightVectorAProjection > 0) {
            controlVariables[VARIABLE_STEERING] = 1;
        } else if (rightVectorAProjection < 0) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else {
            controlVariables[VARIABLE_STEERING] = 0;
        }

        // Acceleration
        controlVariables[VARIABLE_BRAKE] = 0;

        double directionProjection = VectorMath.dotProduct(acceleration, velocityVector);

        double[] reverseNormalizedDirectionVector = VectorMath.multiply(velocityVector, -1);
        double reverseProjection = VectorMath.dotProduct(acceleration, reverseNormalizedDirectionVector);

        if (directionProjection > 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else if (directionProjection < 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else {
            controlVariables[VARIABLE_THROTTLE] = 1;
        }
    }

    private double[] seek(Car self, GameObject target) {
        double[] distance = VectorMath.distance(self, target);
        double[] normalizedDistance = VectorMath.divide(distance, VectorMath.absolute(distance));
        return VectorMath.multiply(normalizedDistance, 5); // FIXME max acceleration?
    }

}
