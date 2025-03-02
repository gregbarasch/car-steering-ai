package carsteering.engine;

/**
 * @author Greg Barasch
 */
public class RayCast {

    public static RayCastResult rayCast(Game game, GameObject subject, double[] target) {

        // Create our ray
        RotatedRectangle ray = new RotatedRectangle(subject.getCollisionBox());

        // Calculate unit of movement based on line
        // Max movement of 1/-1 unit (pixel?) on either axis.. scale second axis down
        // TODO would be more efficient if I changed this to move 1 full car unit at a time along the line
        double dx = target[0] - subject.getX();
        double dy = target[1] - subject.getY();
        double xUnit;
        double yUnit;
        if (Math.abs(dx) > Math.abs(dy)) {
            yUnit = dy == 0 ? 0 : dy / (dx/Math.signum(dx));
            xUnit = dx == 0 ? 0 : Math.signum(dx);
        } else {
            yUnit = dy == 0 ? 0 : Math.signum(dy);
            xUnit = dx == 0 ? 0 :  dx / (dy/Math.signum(dy));
        }

        // Start at the origin
        double[] origin = subject.getXY();
        do {
            // Iterate forward 1 unit
            ray.C.add(xUnit, yUnit);

            // Check if we've collided with something other than ourself
            GameObject collision = game.collision(ray);
            if (collision != null && !collision.equals(subject)) {
                return new RayCastResult(true, ray.C.getXY(), collision);
            }

            // Check if we've surpassed our destination
        } while (!surpassed(origin, target, ray.C.getXY()));

        return new RayCastResult(false, ray.C.getXY(), null);
    }

    /**
     * Check if point is touching or beyond the destination along a line from origin to destination
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

        public GameObject collidedWith() {
            return collidedWith;
        }
    }
}
