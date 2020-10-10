package engine;

public class RayCast {

    public static RayCastResult rayCast(Game game, Car subject, double[] target) {
        // Create our ray
        RotatedRectangle ray = new RotatedRectangle(subject.getCollisionBox());

        // Calculate unit of movement based on line
        // Max movement of 1/-1 unit (pixel?) on either axis.. scale second axis down
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
                rayPosition[0] = ray.C.getX();
                rayPosition[1] = ray.C.getY();
                return new RayCastResult(true, rayPosition, collision);
            }
            i++;

            // Check if weve surpassed our destination
            rayPosition[0] = ray.C.getX();
            rayPosition[1] = ray.C.getY();

//            System.out.println("X: o - " + origin[0] + "  t - " + target[0] + "  r - " + rayPosition[0]);
//            System.out.println("Y: o - " + origin[1] + "  t - " + target[1] + "  r - " + rayPosition[1]);
//            System.out.println("SPEED: " + subject.getSpeed());

        } while (!surpassed(origin, target, rayPosition));

        return new RayCastResult(false, rayPosition, null);
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
