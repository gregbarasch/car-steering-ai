package engine;

/**
 * @author Greg Barasch
 */
public class RayCast {

    public static RayCastResult rayCast(Game game, Car subject, double[] target) {

        // Create our ray
        RotatedRectangle ray = new RotatedRectangle(subject.getCollisionBox());

        // Calculate unit of movement based on line
        // Max movement of 1/-1 unit (pixel?) on either axis.. scale second axis down
        double dy = target[1] - subject.getY();
        double dx = target[0] - subject.getX();
        double xUnit;
        double yUnit;
        if (Math.abs(dx) > Math.abs(dy)) {
            yUnit = dy == 0 ? 0 : dy / (dx/Math.signum(dx));
            xUnit = dx == 0 ? 0 : Math.signum(dx);
        } else {
            yUnit = dy == 0 ? 0 : Math.signum(dy);
            xUnit = dx == 0 ? 0 :  dx / (dy/Math.signum(dy));
        }

        // move the ray forward and check for collision
        double[] origin = new double[]{ subject.getX(), subject.getY() };
        do {
            ray.C.add(xUnit, yUnit);

            // Check if we've collided with something other than ourself
            GameObject collision = game.collision(ray);
            if (collision != null && !collision.equals(subject)) {
                return new RayCastResult(true, ray.C.getXY(), collision);
            }

            // Check if weve surpassed our destination
        } while (!surpassed(origin, target, ray.C.getXY()));

        return new RayCastResult(false, ray.C.getXY(), null);
    }

    /**
     * Check if point is beyond destination along line from origin to destination
     */
    private static boolean surpassed(double[] origin, double[] destination, double[] point) {
        // check if we surpass on the x axis
        if ((origin[0] <= destination[0] &&  destination[0] <= point[0]) ||
                (origin[0] >= destination[0] && destination[0]  >= point[0])) {

            // check if we surpass on the y axis
            return (origin[1] <= destination[1] && destination[1] <= point[1]) ||
                    (origin[1] >= destination[1] && destination[1] >= point[1]);
        }

        return false;
    }

    /**
     * A lil DTO class to help with raycasting
     */
    public static class RayCastResult {

        private final boolean isCollision;
        private final double[] rayLocation;
        private final GameObject collidedWith;

        public RayCastResult(boolean isCollision, double[] rayLocation, GameObject collidedWith) {
            this.isCollision = isCollision;
            this.rayLocation = rayLocation;
            this.collidedWith = collidedWith;
        }

        public boolean isCollision() {
            return isCollision;
        }

        public double[] getRayLocation() {
            return rayLocation;
        }

        public GameObject getCollidedWith() {
            return collidedWith;
        }
    }
}
