package uk.ac.cam.teamhotel.historyphone.ble;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Singleton class to scan for beacons.
 */
public class BeaconScanner {

    private static BeaconScanner instance;

    public static BeaconScanner getInstance() {
        if (instance == null) {
            instance = new BeaconScanner();
        }
        return instance;
    }

    private Observable<ArrayList<Beacon>> beaconStream;
    private AtomicBoolean scanning;

    /**
     * Create a new beacon stream, ready to emit beacons once scanning is started.
     */
    private BeaconScanner() {
        scanning = new AtomicBoolean(false);
        beaconStream = Observable
                .interval(1000, TimeUnit.MILLISECONDS, Schedulers.io())
                .filter(tick -> {
                    // Only forward ticks along the stream while we are scanning.
                    return scanning.get();
                })
                .map(tick -> {
                    // TODO: Poll via the beacons library, once we know which we're using.
                    ArrayList<Beacon> beacons = new ArrayList<>();
                    beacons.add(new Beacon(0L,   34.0f));
                    beacons.add(new Beacon(453L, 43.0f));
                    beacons.add(new Beacon(23L,  10.0f));
                    return beacons;
                });
    }

    /**
     * Begin scanning for beacons on the next tick.
     */
    public void start() { scanning.set(true); }

    /**
     * Cease scanning for beacons at the next tick.
     */
    public void stop() { scanning.set(false); }

    /**
     * @return whether the scanner is currently scanning for beacons.
     */
    public boolean isScanning() { return scanning.get(); }

    /**
     * @return a reference to the beacon stream for observation purposes.
     */
    public Observable<ArrayList<Beacon>> getBeaconStream() { return beaconStream; }
}
