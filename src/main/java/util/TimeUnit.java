package util;

public enum TimeUnit {
    MILLISECONDS(1),
    SECONDS(1000),
    MINUTES(60_000),
    HOURS(3_600_000);

    private final long millis;

    TimeUnit(long millis) {
        this.millis = millis;
    }

    public long toMillis(long duration) {
        return duration * millis;
    }
}