package uk.ac.cam.teamhotel.historyphone.ble;

public class BluetoothNotSupportedException extends Exception {
    public BluetoothNotSupportedException() {
        super("Bluetooth not supported on this device.");
    }
}
