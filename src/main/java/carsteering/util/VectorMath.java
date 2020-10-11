package carsteering.util;

import carsteering.engine.GameObject;

/**
 * @author Greg Barasch
 */
public class VectorMath {

    public static double[] distance(GameObject from, GameObject to) {
        double x = to.getX() - from.getX();
        double y = to.getY() - from.getY();
        return new double[]{ x, y };
    }

    public static double[] distance(GameObject from, double[] to) {
        double x = to[0] - from.getX();
        double y = to[1] - from.getY();
        return new double[]{ x, y };
    }

    public static double[] distance(double[] from, double[] to) {
        double x = to[0] - from[0];
        double y = to[1] - from[1];
        return new double[]{ x, y };
    }

    public static double euclideanDistance(double[] from, double[] to) {
        double sum = 0.0;
        for (int i = 0; i < from.length; i++) {
            sum += (to[i] - from[i]) * (to[i] - from[i]);
        }
        return Math.sqrt(sum);
    }

    public static double[] subtract(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static double[] divide(double[] vector, double divisor) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i]/divisor;
        }
        return result;
    }

    public static double[] multiply(double[] vector, double multiplier) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i]*multiplier;
        }
        return result;
    }

    public static double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    public static double magnitude(double[] vector) {
        double sum = 0;
        for (double element : vector) {
            sum += (element*element);
        }
        return Math.sqrt(sum);
    }

    public static double[] normalize(double[] vector) {
        return VectorMath.divide(vector, VectorMath.magnitude(vector));
    }
}
