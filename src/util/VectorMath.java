package util;

import engine.GameObject;

public class VectorMath {
    public static double[] distance(GameObject a, GameObject b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        return new double[]{ x, y };
    }

    public static double[] divide(double[] vector, double divisor) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= divisor;
        }
        return vector;
    }

    public static double[] multiply(double[] vector, double multiplier) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= multiplier;
        }
        return vector;
    }

    public static double dotProduct(double[] a, double[] b)
    {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    public static double absolute(double[] vector) {
        double sum = 0;
        for (double element : vector) {
            sum += element*element;
        }
        return Math.sqrt(sum);
    }

    public static double[] normalize(double[] vector) {
        double sum = 0;
        for (double element : vector) {
            sum += element;
        }

        for (int i = 0; i < vector.length; i++) {
            vector[i] /= sum;
        }

        return vector;
    }
}
