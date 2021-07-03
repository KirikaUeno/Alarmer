package data;

public class Interval {

    public final double min;
    public final double max;

    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public boolean isIn (double val) {
        return  val>min && val<max;
    }

    @Override
    public String toString() {
        return "("  + min + ";" + max + ')';
    }
}
