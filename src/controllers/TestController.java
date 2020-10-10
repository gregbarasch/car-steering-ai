package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.RotatedRectangle;
import util.VectorMath;

public class TestController extends Controller {

    private final GameObject target;

    public TestController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {

        // Raycast in direction of velocity for 3 time iterations
        double mx = subject.getX() + Math.cos(subject.getAngle()) * subject.getSpeed() * delta_t * 3;
        double my = subject.getY() + Math.sin(subject.getAngle()) * subject.getSpeed() * delta_t * 3;

        // Get acceleration vector
        double[] accelerationVector = seekWithWallAvoidance(subject, target);

        // Calculate the direction projection
        double[] forwardVector = { Math.cos(subject.getAngle()), Math.sin(subject.getAngle()) };
        double directionProjection = VectorMath.dotProduct(accelerationVector, forwardVector);

        // Use direction projection to set our linear acceleration
        if (directionProjection > 0) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        } else {
            controlVariables[VARIABLE_THROTTLE] = -1;
        }
        controlVariables[VARIABLE_BRAKE] = 0;

        // project right vector over acceleration vector to compute steering projection..
        double[] rightVector = new double[]{ forwardVector[1], forwardVector[0]*-1 };
        double steerProjection = VectorMath.dotProduct(accelerationVector, rightVector);

        // Steering
        if (steerProjection > 0) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else {
            controlVariables[VARIABLE_STEERING] = 1;
        }
    }

    private double[] seekWithWallAvoidance(Car subject, GameObject target) {
        double[] distance = VectorMath.distance(subject, target);
        subject.setDebugVector(distance);
        double[] normalizedDistance = VectorMath.normalize(distance);
        return VectorMath.multiply(normalizedDistance, 250); // FIXME max acceleration? hardcoded is fine for now
    }
}
