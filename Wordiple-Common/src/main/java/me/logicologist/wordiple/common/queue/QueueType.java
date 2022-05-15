package me.logicologist.wordiple.common.queue;

public enum QueueType {

    COMPETITIVE() {
        @Override
        public String getName() {
            return "competitive";
        }
    },
    CASUAL() {
        @Override
        public String getName() {
            return "Casual";
        }
    },
    TIME_ROYALE() {
        @Override
        public String getName() {
            return "Time Royale";
        }
    };

    public abstract String getName();
}
