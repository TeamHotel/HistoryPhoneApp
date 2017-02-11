package uk.ac.cam.teamhotel.historyphone;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;

import io.reactivex.observers.TestObserver;
import uk.ac.cam.teamhotel.historyphone.ble.Beacon;
import uk.ac.cam.teamhotel.historyphone.ble.BeaconScanner;

public class BeaconScannerTest {

    @Test
    public void testBeaconStream() throws Exception {
        // TODO: redesign as repeatable test
        //       (create dummy poller which emits predetermined beacons).
        BeaconScanner scanner = BeaconScanner.getInstance();
        TestObserver<Beacon[]> observer = new TestObserver<Beacon[]>() {
            @Override
            public void onNext(Beacon[] beacons) {
                Assert.assertTrue(Arrays.deepEquals(beacons, new Beacon[] { null, null, null }));
                super.onNext(beacons);
            }
        };

        scanner.subscribe(observer);
        observer.assertSubscribed();

        // Test { null, null, null } is emitted repeatedly.
        scanner.start();
        Thread.sleep(2500);
        Assert.assertTrue(observer.valueCount() >= 2);

        // Test nothing is emitted after polling stops.
        scanner.stop();
        int count = observer.valueCount();
        Thread.sleep(1500);
        observer.assertValueCount(count);
    }
}
