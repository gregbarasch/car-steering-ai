package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.CarOutputFilter;
import carsteering.engine.Game;
import carsteering.engine.GameObject;
import carsteering.engine.RayCast;
import carsteering.engine.RayCast.RayCastResult;
import carsteering.util.VectorMath;

public class WallAvoidanceSeekController extends Controller {

    private static final double RAD45 = Math.PI/4;
    private final GameObject target;

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Use raycast to scan for walls... Return our target
        double[] seekTarget = rayScan(subject, game, delta_t);

        // Get acceleration vector
        double[] accelerationVector = seekWithWallAvoidance(subject, seekTarget, delta_t);

        // Filter our acceleration and set our control variables
        CarOutputFilter carOutputFilter = new CarOutputFilter(subject, accelerationVector, delta_t);
        controlVariables[VARIABLE_STEERING] = carOutputFilter.getSteering();
        controlVariables[VARIABLE_THROTTLE] = carOutputFilter.getThrottle();
        controlVariables[VARIABLE_BRAKE] = carOutputFilter.getBrake();
    }

    private double[] seekWithWallAvoidance(Car subject, double[] target, double delta_t) {
        double[] distance = VectorMath.distance(subject, target);
        double[] normalizedDistance = VectorMath.normalize(distance);
        return VectorMath.multiply(normalizedDistance, subject.getMaxVelocity()/delta_t);
    }

    /**
     * Raycast forward (along the path of velocity), left, and right
     */
    private double[] rayScan(Car subject, Game game, double delta_t) {

        // Number of iterations forward for cars current velocity
        double searchDistance = 35;

        // seek target might not be the actual target if a wall is in the way... Init to target
        double[] seekTarget = target.getXY();
        RayCastResult targetResult = RayCast.rayCast(game, subject, seekTarget);

        // Project forward, left and right
        // Project 45 degrees in either direction (left and right)
        // TODO validate if left/right are semantically correct
        RayCastResult forwardResult = rayCast(game, subject, subject.getAngle(), searchDistance, delta_t);
        RayCastResult leftResult = rayCast(game, subject, subject.getAngle()+RAD45, searchDistance, delta_t);
        RayCastResult rightResult = rayCast(game, subject, subject.getAngle()-RAD45, searchDistance, delta_t);

        // Lets make some rules...
        //
        // If we can raycast directly to the target with no collisions, lets do that (default)
        // Let's also go towards the target if we have  no collisions left/right
        //
        // If we hit something forward, lefts try and favor left/right
        // Otherwise we'll stick with the default (directly at target)
        if (targetResult.isCollision() && !targetResult.getCollidedWith().equals(target)) {

            // forward left and right...
            if (leftResult.isCollision() && rightResult.isCollision()) {
                seekTarget = forwardResult.getRayLocation();
            } else if (leftResult.isCollision()) {
                seekTarget = rightResult.getRayLocation();
            } else if (rightResult.isCollision()) {
                seekTarget = leftResult.getRayLocation();
            }
        }

        subject.setDebugDistanceVector(VectorMath.distance(subject, seekTarget));
        return seekTarget;
    }

    private RayCastResult rayCast(Game game, Car subject, double angle, double searchDistance, double delta_t) {
        double mx = Math.cos(angle) * subject.getSpeed() * delta_t * searchDistance;
        double my = Math.sin(angle) * subject.getSpeed() * delta_t * searchDistance;
        double[] checkTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        return RayCast.rayCast(game, subject, checkTarget);
    }
}
