package me.logicologist.wordiple.common.rank;

public class Rank {

    private final String name;
    private final int level;
    private final int minimum;
    private final int maximum;

    public Rank(String name, int level, int minimum, int maximum) {
        this.name = name;
        this.level = level;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public int getMinimum() {
        return this.minimum;
    }

    public int getMaximum() {
        return this.maximum;
    }
}
