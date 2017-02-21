package uk.ac.cam.teamhotel.historyphone.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final String[] permissions = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    ArtifactLoader artifactLoader;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.ViewPager);
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);

        // Create the artifact loader.
        artifactLoader = new ArtifactLoader();

        // Set up the tabbed fragment view.
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        // Get Bluetooth and location permissions.
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]), 0);
        }

        Log.i(TAG, "Activity created.");
    }

    /**
     * Adapter class which provides fragments for the main tabbed layout.
     */
    private class TabAdapter extends FragmentPagerAdapter {
        private String[] titles = { getString(R.string.nearby), getString(R.string.recent) };

        TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    NearbyFragment nearby = new NearbyFragment();
                    nearby.setArtifactLoader(artifactLoader);
                    return nearby;
                case 1:
                    // TODO: Pass artifact loader.
                    return new RecentFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() { return titles.length; }

        @Override
        public CharSequence getPageTitle(int position) { return titles[position]; }

    }
}
