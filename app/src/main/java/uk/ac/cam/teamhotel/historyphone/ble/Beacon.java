package uk.ac.cam.teamhotel.historyphone.ble;

import android.support.v4.util.Pair;

public class Beacon extends Pair<Long, Float> {
    public Beacon(Long uuid, Float distance) {
        super(uuid, distance);
    }

    public Long getUUID() { return first; }
    public Float getDistance() { return second; }

    @Override
    public String toString() {
        return "Beacon(" + String.valueOf(getUUID()) + ", " + String.valueOf(getDistance()) + ")";
    }
}
