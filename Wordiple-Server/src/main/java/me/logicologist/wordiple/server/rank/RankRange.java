package me.logicologist.wordiple.server.rank;

import java.util.ArrayList;
import java.util.List;

public class RankRange {

    private static RankRange instance;

    private final List<Rank> ranks = new ArrayList<>();

    public RankRange() {
        this.ranks.add(new Rank("Delta I", 1, 0, 200, 3));
        this.ranks.add(new Rank("Delta II", 2, 201, 400, 3));
        this.ranks.add(new Rank("Delta III", 3, 401, 600, 3));
        this.ranks.add(new Rank("Gamma I", 1, 601, 800, 4));
        this.ranks.add(new Rank("Gamma II", 2, 801, 1000, 4));
        this.ranks.add(new Rank("Gamma III", 3, 1001, 1200, 4));
        this.ranks.add(new Rank("Beta I", 1, 1201, 1400, 5));
        this.ranks.add(new Rank("Beta II", 2, 1401, 1600, 5));
        this.ranks.add(new Rank("Beta III", 3, 1601, 1800, 5));
        this.ranks.add(new Rank("Alpha I", 1, 1801, 2000, 6));
        this.ranks.add(new Rank("Alpha II", 2, 2001, 2200, 6));
        this.ranks.add(new Rank("Alpha III", 3, 2201, 2400, 7));
        this.ranks.add(new Rank("Sigma", -1, 2401, -1, 7));
        this.ranks.add(new OmegaRank("Omega", -1, 2401, -1, 7));
    }

    public static RankRange getInstance() {
        if (instance == null) {
            instance = new RankRange();
        }
        return instance;
    }

    public Rank getRank(int rank) {
        for (Rank r : this.ranks) {
            boolean valid = r.isValid(rank);
            if (r instanceof OmegaRank) {
                valid = ((OmegaRank) r).isTopTen();
            }
            if (valid) {
                return r;
            }
        }
        return null;
    }
}
