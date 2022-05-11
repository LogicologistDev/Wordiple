package me.logicologist.wordiple.server.rank;

public class OmegaRank extends Rank {

    public OmegaRank(String name, int level, int minimum, int maximum, int competitiveRounds) {
        super(name, level, minimum, maximum, competitiveRounds);
    }

    public boolean isTopTen() {
        return false;
    }
}
