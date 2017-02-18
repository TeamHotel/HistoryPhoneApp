package uk.ac.cam.teamhotel.historyphone.ble;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Random;
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
    private int interval;

    private Pair<Long, ArrayList<Beacon>> lastScan;

    // TODO: More robust fix to stream issue. Shouldn't be using map for scans.
    private ArrayList<Beacon> cachedScan(long tick) {
        // If the requested scan is cached, return it.
        if (lastScan != null && lastScan.first == tick) {
            return lastScan.second;
        }

        // TODO: Replace with a call to a ScanProvider once implemented.
        // Dummy scan for beacons.
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(new Beacon(0L,   0.0f));
        beacons.add(new Beacon(123L, 1.0f));
        for (int i = 0; i < new Random().nextInt(7); i++) {
            beacons.add(new Beacon(23L, 2.0f + i));
        }

        // Cache the current scan.
        lastScan = new Pair<>(tick, beacons);
        return beacons;
    }

    /**
     * Create a new beacon stream, ready to emit beacons once scanning is started.
     */
    private BeaconScanner() {
        scanning = new AtomicBoolean(false);
        interval = 2000;
        beaconStream = Observable
                .interval(interval, TimeUnit.MILLISECONDS, Schedulers.single())
                .filter(tick -> {
                    // Only forward ticks along the stream while we are scanning.
                    return scanning.get();
                })
                .map(this::cachedScan);
    }

    /**
     * Set the scan interval in milliseconds.
     */
    public void setInterval(int interval) { this.interval = interval; }

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
