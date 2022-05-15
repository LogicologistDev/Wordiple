package me.logicologist.wordiple.client.integration;

public class IntegrationStatus {

    private boolean resetTimer;
    private String state;
    private String details;

    public IntegrationStatus setTimer() {
        this.resetTimer = true;
        return this;
    }

    public boolean isResetTimer() {
        return this.resetTimer;
    }

    public IntegrationStatus setState(String state) {
        this.state = state;
        return this;
    }

    public String getState() {
        return this.state;
    }

    public IntegrationStatus setDetails(String details) {
        this.details = details;
        return this;
    }

    public String getDetails() {
        return this.details;
    }
}
