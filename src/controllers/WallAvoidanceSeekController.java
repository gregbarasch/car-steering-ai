package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.RayCast;
import engine.RayCast.RayCastResult;
import util.VectorMath;

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
        double[] accelerationVector = seekWithWallAvoidance(subject, seekTarget);

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

    private double[] seekWithWallAvoidance(Car subject, double[] target) {
        double[] distance = VectorMath.distance(subject, target);
        double[] normalizedDistance = VectorMath.normalize(distance);
        return VectorMath.multiply(normalizedDistance, subject.getMaxVelocity()); // FIXME max acceleration? hardcoded is fine for now
    }

    /**
     * Raycast forward (along the path of velocity), left, and right
     */
    private double[] rayScan(Car subject, Game game, double delta_t) {
        // Number of iterations forward for cars current velocity
        double searchDistance = 30;

        // seek target might not be the actual target if a wall is in the way... Init to target
        double[] seekTarget = new double[]{ target.getX(), target.getY() };
        RayCastResult targetResult = RayCast.rayCast(game, subject, seekTarget);

        // Project forward
        double angle = subject.getAngle();
        double mx = Math.cos(angle) * subject.getSpeed() * delta_t * searchDistance;
        double my = Math.sin(angle) * subject.getSpeed() * delta_t * searchDistance;
        double[] forwardCheckTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        RayCastResult forwardResult = RayCast.rayCast(game, subject, forwardCheckTarget);

        // Project 45 degrees (converted to radians) in a direction. lets just call it "left" (didnt actually check if that's correct)
        angle = subject.getAngle() + RAD45;
        mx = Math.cos(angle) * subject.getSpeed() * delta_t * searchDistance;
        my = Math.sin(angle) * subject.getSpeed() * delta_t * searchDistance;
        double[] leftCheckTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        RayCastResult leftResult = RayCast.rayCast(game, subject, leftCheckTarget);

        // Project 45 degrees in the other direction... lets just call it "right" (didnt actually check if that's correct)
        angle = subject.getAngle() - RAD45;
        mx = Math.cos(angle) * subject.getSpeed() * delta_t * searchDistance;
        my = Math.sin(angle) * subject.getSpeed() * delta_t * searchDistance;
        double[] rightCheckTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        RayCastResult rightResult = RayCast.rayCast(game, subject, rightCheckTarget);

        // Lets make up some rules...
        //
        // Clearly, if we can raycast directly to the target with no collisions, lets do that (default)
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

        subject.setDebugVector(VectorMath.distance(subject, seekTarget));
        return seekTarget;
    }
}
