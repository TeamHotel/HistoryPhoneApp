package uk.ac.cam.teamhotel.historyphone.ble;

public class Beacon {

    private final long uuid;
    private final float distance;

    public Beacon(long uuid, float distance) {
        this.uuid = uuid;
        this.distance = distance;
    }

    public long getUUID() {
        return uuid;
    }

    public float getDistance() {
        return distance;
    }
}
