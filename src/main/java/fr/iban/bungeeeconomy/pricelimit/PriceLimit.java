package fr.iban.bungeeeconomy.pricelimit;

public class PriceLimit {

    private double min;
    private double max;

    public PriceLimit(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean isInLimits(double price) {
        return (min == 0 || price >= min) && (max == 0 || price <= max);
    }

    public boolean isInLimits(double price, int amount) {
        price = price/amount;
        return isInLimits(price);
    }
}
