package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.RotatedRectangle;
import util.VectorMath;

public class WallAvoidanceSeekController extends Controller {

    private final GameObject target;

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // seek target might not be the actual target if a wall is in the way
        double[] seekTarget = new double[]{ subject.getX(), subject.getY() };

        // Raycast in direction of velocity, several time iterations forward
        double angle = subject.getAngle();
        double mx = Math.cos(angle) * subject.getSpeed() * delta_t * 20;
        double my = Math.sin(angle) * subject.getSpeed() * delta_t * 20;
        double[] checkTarget = new double[]{ subject.getX()+mx, subject.getY()+my };
        boolean isCollision = rayCast(game, subject, checkTarget);

        if (isCollision) {
            // Project 45 degrees (converted to radians) in either direction
            double rad45 = Math.PI/4;
            angle = subject.getAngle() + rad45;
            mx = Math.cos(angle) * subject.getSpeed() * delta_t * 20;
            my = Math.sin(angle) * subject.getSpeed() * delta_t * 20;
            checkTarget[0] = subject.getX()+mx;
            checkTarget[1] = subject.getY()+my;
            isCollision = rayCast(game, subject, checkTarget);

            // Project 45 degrees in the other direction
            if (isCollision) {
                angle = subject.getAngle() - rad45;
                mx = Math.cos(angle) * subject.getSpeed() * delta_t * 20;
                my = Math.sin(angle) * subject.getSpeed() * delta_t * 20;
                checkTarget[0] = subject.getX()+mx;
                checkTarget[1] = subject.getY()+my;
                isCollision = rayCast(game, subject, checkTarget);
                if (!isCollision) seekTarget = checkTarget;
            } else {
                seekTarget = checkTarget;
            }
        } else {
            seekTarget = checkTarget;
        }

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
        return VectorMath.multiply(normalizedDistance, 250); // FIXME max acceleration? hardcoded is fine for now
    }

    private boolean rayCast(Game game, Car subject, double[] target) {
        // Create our ray
        RotatedRectangle ray = new RotatedRectangle(subject.getCollisionBox());

        // Calculate unit of movement based on line
        double y_ = target[1] - subject.getY();
        double x_ = target[0] - subject.getX();
        double xUnit;
        double yUnit;
        if (Math.abs(x_) > Math.abs(y_)) {
            yUnit = y_ == 0 ? 0 : y_ / (x_/Math.signum(x_));
            xUnit = x_ == 0 ? 0 : Math.signum(x_);
        } else {
            yUnit = y_ == 0 ? 0 : Math.signum(y_);
            xUnit = x_ == 0 ? 0 :  x_ / (y_/Math.signum(y_));
        }

//        System.out.println("X: " + target[0] + " XUNIT: " + xUnit);
//        System.out.println("Y: " + target[1] + " YUNIT: " + yUnit);

        // move the ray forward and check for collision
        double[] origin = new double[]{ subject.getX(), subject.getY() };
        double[] rayPosition = new double[]{ ray.C.getX(), ray.C.getY() };
        int i = 0;
        do {

            ray.C.add(xUnit, yUnit);

            GameObject collision = game.collision(ray);
            if (collision != null && !collision.equals(subject)) {
//                System.out.println(collision.toString());
//                System.out.println("INTERATION i " + i);
                return true;
            }
            i++;
            subject.setDebugVector(new double[]{ xUnit*i, yUnit*i });

            // Check if weve surpassed our destination
            rayPosition[0] = ray.C.getX();
            rayPosition[1] = ray.C.getY();

//            System.out.println("X: o - " + origin[0] + "  t - " + target[0] + "  r - " + rayPosition[0]);
//            System.out.println("Y: o - " + origin[1] + "  t - " + target[1] + "  r - " + rayPosition[1]);
//            System.out.println("SPEED: " + subject.getSpeed());

        } while (!surpassed(origin, target, rayPosition));

        return false;
    }

    private boolean surpassed(double[] origin, double[] destination, double[] point) {
        // check if we surpass on the x axis
        if ((origin[0] <= destination[0] &&  destination[0] <= point[0]) ||
                (origin[0] >= destination[0] && destination[0]  >= point[0])) {
            return true;
        }

        // check if we surpass on the y axis
        return (origin[1] <= destination[1] && destination[1] <= point[1]) ||
                (origin[1] >= destination[1] && destination[1] >= point[1]);
    }
}
