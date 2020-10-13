package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.CarOutputFilter;
import carsteering.engine.Game;
import carsteering.engine.GameObject;
import carsteering.engine.RayCast;
import carsteering.engine.RayCast.RayCastResult;
import carsteering.util.VectorMath;

import java.util.concurrent.ThreadLocalRandom;

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
     * Raycast forward (along the path of velocity), left, and right, as well as directly at the target
     * @return x/y coordinates of destination
     */
    private double[] rayScan(Car subject, Game game, double delta_t) {

        // Number of iterations forward for cars current velocity
        double searchDistance = 30;

        // seek target might not be the actual target if a wall is in the way... Init to target
        double[] seekTarget = target.getXY();
        RayCastResult targetResult = RayCast.rayCast(game, subject, seekTarget);

        // Project forward, left and right
        // Project 45 degrees in either direction (left and right)
        // TODO validate if left/right are semantically correct
        RayCastResult forwardResult = rayCast(game, subject, subject.getAngle(), searchDistance/1.5, delta_t);
        RayCastResult leftResult = rayCast(game, subject, subject.getAngle()+RAD45, searchDistance, delta_t);
        RayCastResult rightResult = rayCast(game, subject, subject.getAngle()-RAD45, searchDistance, delta_t);

        // Lets make some rules...
        // If we are on a path to crash into something (that's not the target) withihn
        if (forwardResult.isCollision() && !forwardResult.collidedWith().equals(target)) {

            // If forward/left/right are blocked then lets reverse
            // If one of our directions are blocked, move the other direction
            // Otherwise, move towards the target
            if (leftResult.isCollision()) {
                seekTarget = rightResult.getRayLocation();
            } else if (rightResult.isCollision()) {
                seekTarget = leftResult.getRayLocation();
            }

        } else {

            // Forward isn't blocked, so lets move forward/left/right if we can't see the target
            if (targetResult.isCollision() && !targetResult.collidedWith().equals(target)) {

                if (leftResult.isCollision() && rightResult.isCollision()) {
                    seekTarget = forwardResult.getRayLocation();
                } else if (leftResult.isCollision()) {
                    seekTarget = rightResult.getRayLocation();
                } else if (rightResult.isCollision()) {
                    seekTarget = leftResult.getRayLocation();
                }

            }
        }

        subject.setDebugDistanceVector(VectorMath.distance(subject, seekTarget));
        return seekTarget;
    }

    /**
     * Raycast at specific angle given subjects current speed
     */
    private RayCastResult rayCast(Game game, Car subject, double angle, double searchDistance, double delta_t) {
        // If our speed is 0, lets cast randomly forward or backwards
        double speed = subject.getSpeed() == 0 ? ThreadLocalRandom.current().nextDouble(-1.0, 1.01) : subject.getSpeed();
        double mx = Math.cos(angle) * speed * delta_t * searchDistance;
        double my = Math.sin(angle) * speed *  delta_t * searchDistance;
        double[] checkTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        return RayCast.rayCast(game, subject, checkTarget);
    }
}
