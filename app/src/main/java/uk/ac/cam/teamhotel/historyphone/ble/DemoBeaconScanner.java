package uk.ac.cam.teamhotel.historyphone.ble;

import android.util.Log;

import java.util.Random;

import io.reactivex.ObservableEmitter;

public class DemoBeaconScanner extends BeaconScanner {

    public static final String TAG = "DemoBeaconScanner";

    protected DemoBeaconScanner() {
        super();
    }

    private final Runnable action = () -> {
        Log.i(TAG, "Started spawning fake beacons.");

        try {
            Random random = new Random();
            while (!Thread.interrupted()) {
                // Delay before emitting the next scan.
                int delay = random.nextInt(500) + 200;
                Thread.sleep(delay);

                // Generate a random beacon to emit.
                long uuid = random.nextInt(4) + 57L;
                float distance = uuid - 50 + random.nextInt(20) / 10.0f;
                Beacon beacon = new Beacon(uuid, distance);
                for (ObservableEmitter<Beacon> emitter : getEmitters()) {
                    emitter.onNext(beacon);
                }
                Log.d(TAG, "Spawned beacon: " + beacon.toString());
            }
        } catch (InterruptedException e) {
            // Ignored.
        }

        Log.i(TAG, "Stopped spawning fake beacons.");
    };
    private Thread spawner;

    @Override
    public void start() throws BluetoothNotSupportedException {
        if (isScanning()) {
            return;
        }

        spawner = new Thread(action);
        spawner.start();

        super.start();
    }

    @Override
    public void stop() {
        if (!isScanning()) {
            return;
        }

        spawner.interrupt();

        super.stop();
    }
}
