package uk.ac.cam.teamhotel.historyphone.ble;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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

    private Observable<Beacon[]> beaconStream;
    private AtomicBoolean scanning;

    /**
     * Create a new beacon stream, ready to emit beacons once scanning is started.
     */
    private BeaconScanner() {
        scanning = new AtomicBoolean(false);
        beaconStream = Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long l) throws Exception {
                        // Only forward ticks along the stream while we are scanning.
                        return scanning.get();
                    }
                })
                .map(new Function<Long, Beacon[]>() {
                    @Override
                    public Beacon[] apply(Long l) throws Exception {
                        // TODO: Poll via the beacons library, once we know which we're using.
                        return new Beacon[] { null, null, null };
                    }
                });
    }

    public void start() {
        scanning.set(true);
    }

    public void stop() {
        scanning.set(false);
    }

    public Observable<Beacon[]> getBeaconStream() {
        return beaconStream;
    }
}
