package me.logicologist.wordiple.common.rank;

import java.util.ArrayList;
import java.util.List;

public class RankRange {

    private final List<Rank> ranks = new ArrayList<>();

    public RankRange() {
        this.ranks.add(new Rank("Delta I", 1, 0, 200));
        this.ranks.add(new Rank("Delta II", 2, 201, 400));
        this.ranks.add(new Rank("Delta III", 3, 401, 600));
        this.ranks.add(new Rank("Gamma I", 1, 601, 800));
        this.ranks.add(new Rank("Gamma II", 2, 801, 1000));
        this.ranks.add(new Rank("Gamma III", 3, 1001, 1200));
        this.ranks.add(new Rank("Beta I", 1, 1201, 1400));
        this.ranks.add(new Rank("Beta II", 2, 1401, 1600));
        this.ranks.add(new Rank("Beta III", 3, 1601, 1800));
        this.ranks.add(new Rank("Alpha I", 1, 1801, 2000));
        this.ranks.add(new Rank("Alpha II", 2, 2001, 2200));
        this.ranks.add(new Rank("Alpha III", 3, 2201, 2400));
        this.ranks.add(new Rank("Sigma", -1, 2401, -1));
        this.ranks.add(new Rank("Omega", -1, 2401, -1));
    }
}
