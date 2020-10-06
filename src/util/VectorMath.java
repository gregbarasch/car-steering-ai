package util;

import engine.GameObject;

public class VectorMath {
    public static double[] distance(GameObject a, GameObject b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        return new double[]{ x, y };
    }

    private static double distance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
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
