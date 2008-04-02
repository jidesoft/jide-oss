package com.jidesoft.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * A collection of several util methods related to BigDecimal. We only used it in BigDecimalSummaryCalculator in JIDE
 * Pivot Grid. but this class will be reserved as a place holder for methods related to BigDecimal.
 */
public final class BigDecimalMathUtils {
    public static final BigDecimal TWO = BigDecimal.valueOf(2);

    protected BigDecimalMathUtils() {
    }

    /**
     * Returns the sum number in the numbers list.
     *
     * @param numbers the numbers to calculate the sum.
     * @return the sum of the numbers.
     */
    public static BigDecimal sum(List<BigDecimal> numbers) {
        BigDecimal sum = new BigDecimal(0);
        for (BigDecimal bigDecimal : numbers) {
            sum = sum.add(bigDecimal);
        }
        return sum;
    }

    /**
     * Returns the mean number in the numbers list.
     *
     * @param numbers the numbers to calculate the mean.
     * @param context the MathContext.
     * @return the mean of the numbers.
     */
    public static BigDecimal mean(List<BigDecimal> numbers, MathContext context) {
        BigDecimal sum = sum(numbers);
        return sum.divide(new BigDecimal(numbers.size()), context);
    }

    /**
     * Returns the min number in the numbers list.
     *
     * @param numbers the numbers to calculate the min.
     * @return the min number in the numbers list.
     */
    public static BigDecimal min(List<BigDecimal> numbers) {
        return new TreeSet<BigDecimal>(numbers).first();
    }

    /**
     * Returns the max number in the numbers list.
     *
     * @param numbers the numbers to calculate the max.
     * @return the max number in the numbers list.
     */
    public static BigDecimal max(List<BigDecimal> numbers) {
        return new TreeSet<BigDecimal>(numbers).last();
    }

    /**
     * Returns the standard deviation of the numbers.
     * <p/>
     * Double.NaN is returned if the numbers list is empty.
     *
     * @param numbers       the numbers to calculate the standard deviation.
     * @param biasCorrected true if variance is calculated by dividing by n - 1. False if by n. stddev is a sqrt of the
     *                      variance.
     * @param context       the MathContext
     * @return the standard deviation
     */
    public static BigDecimal stddev(List<BigDecimal> numbers, boolean biasCorrected, MathContext context) {
        BigDecimal stddev;
        int n = numbers.size();
        if (n > 0) {
            if (n > 1) {
                stddev = sqrt(var(numbers, biasCorrected, context));
            }
            else {
                stddev = BigDecimal.ZERO;
            }
        }
        else {
            stddev = BigDecimal.valueOf(Double.NaN);
        }
        return stddev;

    }

    /**
     * Computes the variance of the available values. By default, the unbiased "sample variance" definitional formula is
     * used: variance = sum((x_i - mean)^2) / (n - 1)
     * <p/>
     * The "population variance"  ( sum((x_i - mean)^2) / n ) can also be computed using this statistic.  The
     * <code>biasCorrected</code> property determines whether the "population" or "sample" value is returned by the
     * <code>evaluate</code> and <code>getResult</code> methods. To compute population variances, set this property to
     * <code>false</code>.
     *
     * @param numbers       the numbers to calculate the variance.
     * @param biasCorrected true if variance is calculated by dividing by n - 1. False if by n.
     * @param context       the MathContext
     * @return the variance of the numbers.
     */
    public static BigDecimal var(List<BigDecimal> numbers, boolean biasCorrected, MathContext context) {
        int n = numbers.size();
        if (n == 0) {
            return BigDecimal.valueOf(Double.NaN);
        }
        else if (n == 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal mean = mean(numbers, context);
        List<BigDecimal> squares = new ArrayList<BigDecimal>();
        for (BigDecimal number : numbers) {
            BigDecimal XminMean = number.subtract(mean);
            squares.add(XminMean.pow(2, context));
        }
        BigDecimal sum = sum(squares);
        return sum.divide(new BigDecimal(biasCorrected ? numbers.size() - 1 : numbers.size()), context);

    }

    /**
     * Calcualtes the square root of the number.
     *
     * @param number the input number.
     * @return the square root of the input number.
     */
    public static BigDecimal sqrt(BigDecimal number) {
        int digits; // final precision
        BigDecimal numberToBeSquareRooted;
        BigDecimal iteration1;
        BigDecimal iteration2;
        BigDecimal temp1 = null;
        BigDecimal temp2 = null; // temp values

        int extraPrecision = number.precision();
        MathContext mc = new MathContext(extraPrecision, RoundingMode.HALF_UP);
        numberToBeSquareRooted = number;                                   // bd global variable
        double num = numberToBeSquareRooted.doubleValue();             // bd to double

        if (mc.getPrecision() == 0)
            throw new IllegalArgumentException("\nRoots need a MathContext precision > 0");
        if (num < 0.)
            throw new ArithmeticException("\nCannot calculate the square root of a negative number");
        if (num == 0.)
            return number.round(mc);                    // return sqrt(0) immediately

        if (mc.getPrecision() < 50)                // small precision is buggy..
            extraPrecision += 10;                    // ..make more precise
        int startPrecision = 1;                   // default first precision

        /* create the initial values for the iteration procedure:
        * x0:  x ~ sqrt(d)
        * v0:  v = 1/(2*x)
        */
        if (num == Double.POSITIVE_INFINITY)       // d > 1.7E308
        {
            BigInteger bi = numberToBeSquareRooted.unscaledValue();
            int biLen = bi.bitLength();
            int biSqrtLen = biLen / 2;                // floors it too

            bi = bi.shiftRight(biSqrtLen);          // bad guess sqrt(d)
            iteration1 = new BigDecimal(bi);                 // x ~ sqrt(d)

            MathContext mm = new MathContext(5, RoundingMode.HALF_DOWN);   // minimal precision
            extraPrecision += 10;                   // make up for it later

            iteration2 = BigDecimal.ONE.divide(TWO.multiply(iteration1, mm), mm);   // v = 1/(2*x)
        }
        else                                      // d < 1.7E10^308  (the usual numbers)
        {
            double s = Math.sqrt(num);
            iteration1 = new BigDecimal(s);                  // x = sqrt(d)
            iteration2 = new BigDecimal(1. / 2. / s);            // v = 1/2/x
            // works because Double.MIN_VALUE * Double.MAX_VALUE ~ 9E-16, so: v > 0

            startPrecision = 64;
        }

        digits = mc.getPrecision() + extraPrecision;        // global limit for procedure

        // create initial MathContext(precision, RoundingMode)
        MathContext n = new MathContext(startPrecision, mc.getRoundingMode());

        return sqrtProcedure(n, digits, numberToBeSquareRooted, iteration1, iteration2, temp1, temp2);           // return square root using argument precision
    }

    /**
     * Square root by coupled Newton iteration, sqrtProcedure() is the iteration part I adopted the Algorithm from the
     * book "Pi-unleashed", so now it looks more natural I give sparse math comments from the book, it assumes argument
     * mc precision >= 1
     *
     * @param mc
     * @param digits
     * @param numberToBeSquareRooted
     * @param iteration1
     * @param iteration2
     * @param temp1
     * @param temp2
     * @return
     */
    @SuppressWarnings({"JavaDoc"})
    private static BigDecimal sqrtProcedure(MathContext mc, int digits, BigDecimal numberToBeSquareRooted, BigDecimal iteration1,
                                            BigDecimal iteration2, BigDecimal temp1, BigDecimal temp2) {
        // next v                                         // g = 1 - 2*x*v
        temp1 = BigDecimal.ONE.subtract(TWO.multiply(iteration1, mc).multiply(iteration2, mc), mc);
        iteration2 = iteration2.add(temp1.multiply(iteration2, mc), mc); // v += g*v        ~ 1/2/sqrt(d)

        // next x
        temp2 = numberToBeSquareRooted.subtract(iteration1.multiply(iteration1, mc), mc); // e = d - x^2
        iteration1 = iteration1.add(temp2.multiply(iteration2, mc), mc); // x += e*v        ~ sqrt(d)

        // increase precision
        int m = mc.getPrecision();
        if (m < 2)
            m++;
        else
            m = m * 2 - 1; // next Newton iteration supplies so many exact digits

        if (m < 2 * digits) // digits limit not yet reached?
        {
            mc = new MathContext(m, mc.getRoundingMode()); // apply new precision
            sqrtProcedure(mc, digits, numberToBeSquareRooted, iteration1, iteration2, temp1, temp2); // next iteration
        }

        return iteration1; // returns the iterated square roots
    }

    public static void main(String[] args) {
        System.out.println(sqrt(new BigDecimal("25029.33333")));
    }
}
