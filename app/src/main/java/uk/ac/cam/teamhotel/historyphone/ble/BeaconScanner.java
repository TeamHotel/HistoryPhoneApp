package uk.ac.cam.teamhotel.historyphone.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Singleton class to scan for beacons.
 */
public class BeaconScanner {

    public static final String TAG = "BeaconScanner";

    private static BeaconScanner instance;

    public static BeaconScanner getInstance() {
        if (instance == null) {
            instance = new BeaconScanner();
        }
        return instance;
    }

    private boolean scanning;
    private Observable<Beacon> beaconStream;
    private ObservableEmitter<Beacon> beaconEmitter;
    private BluetoothAdapter adapter;

    /**
     * Create a new beacon stream, ready to emit beacons once scanning is started.
     */
    private BeaconScanner() {
        Log.i(TAG, "Initialising beacon scanner.");
        scanning = false;
        beaconStream = Observable.create(new ObservableOnSubscribe<Beacon>() {
            @Override
            public void subscribe(ObservableEmitter<Beacon> emitter) throws Exception {
                beaconEmitter = emitter;
            }
        });
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Scan callback object used to emit beacons on the beacon stream.
     */
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getScanRecord() == null) {
                Log.w(TAG, "Bad BLE scan received.");
                return;
            }

            int rssi = result.getRssi();
            byte[] record = result.getScanRecord().getBytes();

            // Extract the UUID.
            if (record[18] != -6 || record[19] != -6 || record[20] != -6) {
                Log.v(TAG, "Scanned BLE device: " + result.getScanRecord().getDeviceName() +
                        "@" + rssi + "dBm");
                return;
            }

            long uuid = (record[21] << 1) + record[22];
            float dist = 100.0f - rssi;  // TODO: Replace with actual distance formula.

            Beacon beacon = new Beacon(uuid, dist);
            Log.d(TAG, "Scanned beacon: " + beacon.toString());
            beaconEmitter.onNext(beacon);
        }

        @Override
        public void onScanFailed(int code) {
            Log.e(TAG, "BLE scan failed.");
        }
    };

    /**
     * Begin emitting beacons on the beacon stream.
     */
    public void start() throws BluetoothNotSupportedException {
        // If we're already scanning, we're done.
        if (scanning){
            return;
        }

        // If the adapter is null, Bluetooth is not supported.
        if (adapter == null) {
            Log.e(TAG, "Default Bluetooth adapter is null.");
            throw new BluetoothNotSupportedException();
        }

        // Enable Bluetooth if not already enabled.
        if (!adapter.isEnabled()) {
            Log.w(TAG, "Could not begin scanning for beacons: adapter disabled.");
            return;
        }

        // Get the BLE scanner. If this is null, Bluetooth may not be enabled.
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) {
            Log.w(TAG, "Could not begin scanning for beacons: BLE scanner null.");
            return;
        }
        scanner.startScan(scanCallback);

        scanning = true;
    }

    /**
     * Cease emitting beacons on the beacon stream.
     */
    public void stop() {
        // If we weren't scanning, or Bluetooth is not supported, we're done.
        if (!scanning || adapter == null){
            return;
        }

        // Enable Bluetooth if not already enabled.
        if (!adapter.isEnabled()) {
            scanning = false;
            return;
        }

        // Get the BLE scanner. If this is null, Bluetooth may not be enabled.
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) {
            Log.w(TAG, "Could not retrieve scanner to stop scanning.");
            return;
        }
        scanner.stopScan(scanCallback);

        scanning = false;
    }

    /**
     * @return whether the scanner is currently scanning for beacons.
     */
    public boolean isScanning() { return scanning; }

    /**
     * @return whether Bluetooth is current enabled.
     */
    public boolean isBluetoothEnabled() { return adapter.isEnabled(); }

    /**
     * @return a reference to the beacon stream for observation purposes.
     */
    public Observable<Beacon> getBeaconStream() { return beaconStream; }
}
