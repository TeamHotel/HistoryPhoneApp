package uk.ac.cam.teamhotel.historyphone;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import uk.ac.cam.teamhotel.historyphone.ble.Beacon;
import uk.ac.cam.teamhotel.historyphone.ble.BeaconScanner;

public class BeaconScannerTest {

    private static boolean scansEqual(ArrayList<Beacon> left,
                                      ArrayList<Beacon> right) {
        for (int i = 0; i < left.size(); i++) {
            if (!left.get(i).equals(right.get(i)))
                return false;
        }
        return true;
    }

    @Test
    public void testBeaconStream() throws Exception {
        // TODO: Redesign as repeatable test (create dummy poller which emits set beacons).
        final ArrayList<Beacon> testPairs = new ArrayList<>();
        testPairs.add(new Beacon(0L,   34.0f));
        testPairs.add(new Beacon(453L, 43.0f));
        testPairs.add(new Beacon(23L,  10.0f));

        BeaconScanner scanner = BeaconScanner.getInstance();

        // Count as an array to work-around final restriction on closures.
        final int[] count = { 0 };
        scanner.getBeaconStream().subscribe(beacons -> {
            Assert.assertTrue(scansEqual(beacons, testPairs));
            count[0]++;
        });

        // Test { (0, 34), (453, 43), (23, 10) } is emitted repeatedly.
        scanner.start();
        Thread.sleep(2500);
        Assert.assertTrue(count[0] == 2);

        // Test that scanning stops correctly.
        scanner.stop();
        int before = count[0];
        Thread.sleep(1500);
        Assert.assertEquals(count[0], before);
    }
}
