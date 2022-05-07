package me.logicologist.wordiple.server.rank;

public class OmegaRank extends Rank {

    public OmegaRank(String name, int level, int minimum, int maximum) {
        super(name, level, minimum, maximum);
    }

    public boolean isTopTen() {
        return false;
    }
}
