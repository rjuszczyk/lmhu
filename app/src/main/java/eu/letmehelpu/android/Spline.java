package eu.letmehelpu.android;

public class Spline {
    double mX1;
    double mY1;
    double mX2;
    double mY2;

    public Spline(double mX1, double mY1, double mX2, double mY2) {
        this.mX1 = mX1;
        this.mY1 = mY1;
        this.mX2 = mX2;
        this.mY2 = mY2;
    }

    public double get(double aX) {
        if (mX1 == mY1 && mX2 == mY2) return aX; // linear
        return CalcBezier(GetTForX(aX), mY1, mY2);
    }


    double A(double aA1, double aA2) {
        return 1.0 - 3.0 * aA2 + 3.0 * aA1;
    }

    double B(double aA1, double aA2) {
        return 3.0 * aA2 - 6.0 * aA1;
    }

    double C(double aA1) {
        return 3.0 * aA1;
    }

    // Returns x(t) given t, x1, and x2, or y(t) given t, y1, and y2.
    double CalcBezier(double aT, double aA1, double aA2) {
        return ((A(aA1, aA2) * aT + B(aA1, aA2)) * aT + C(aA1)) * aT;
    }

    // Returns dx/dt given t, x1, and x2, or dy/dt given t, y1, and y2.
    double GetSlope(double aT, double aA1, double aA2) {
        return 3.0 * A(aA1, aA2) * aT * aT + 2.0 * B(aA1, aA2) * aT + C(aA1);
    }

    double GetTForX(double aX) {
        // Newton raphson iteration
        double aGuessT = aX;
        for (double i = 0; i < 4; ++i) {
            double currentSlope = GetSlope(aGuessT, mX1, mX2);
            if (currentSlope == 0.0) return aGuessT;
            double currentX = CalcBezier(aGuessT, mX1, mX2) - aX;
            aGuessT -= currentX / currentSlope;
        }
        return aGuessT;
    }
}
