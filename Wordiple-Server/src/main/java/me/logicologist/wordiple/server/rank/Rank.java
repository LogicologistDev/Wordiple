package me.logicologist.wordiple.server.rank;

public class Rank {

    private final String name;
    private final int level;
    private final int minimum;
    private final int maximum;
    private final int competitiveRounds;

    public Rank(String name, int level, int minimum, int maximum, int competitiveRounds) {
        this.name = name;
        this.level = level;
        this.minimum = minimum;
        this.maximum = maximum;
        this.competitiveRounds = competitiveRounds;
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

    public int getCompetitiveRounds() {
        return this.competitiveRounds;
    }

    public boolean isValid(int rank) {
        return rank >= this.minimum && (this.maximum == -1 || rank <= this.maximum);
    }
}
