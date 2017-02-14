package uk.ac.cam.teamhotel.historyphone.ble;

import android.support.v4.util.Pair;

public class Beacon extends Pair<Long, Float> {
    public Beacon(Long uuid, Float distance) {
        super(uuid, distance);
    }
}
