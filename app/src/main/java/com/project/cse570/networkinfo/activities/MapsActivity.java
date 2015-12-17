package com.project.cse570.networkinfo.activities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.project.cse570.networkinfo.R;
import com.project.cse570.networkinfo.sqlite.FeedReaderContract;
import com.project.cse570.networkinfo.sqlite.NetworkDBHelper;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    String LOG_TAG = "MapsActivity";
    // Declare a variable for the cluster manager.
    private ClusterManager<MapItem> mClusterManager;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

        setUpClusterer();
    }

    private void setUpClusterer() {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.9064395, -73.108219), 13));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MapItem>(this, mMap);
        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        SQLiteDatabase db;
        NetworkDBHelper mNetworkDBHelper = new NetworkDBHelper(this);
        db = mNetworkDBHelper.getReadableDatabase();
        Cursor mCursor;

        mCursor = db.rawQuery("SELECT " + FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS + ", "
                + FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE + ", "
                + FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE + ", "
                + FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS + " FROM "
                + FeedReaderContract.FeedEntry.TABLE_NAME, null);

        while (mCursor.moveToNext()) {
            double lat = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE)));
            double lng = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE)));
            int mNetworkType = 0;
            switch (mCursor.getString(mCursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS))) {
                case "2G":
                    mNetworkType = 1;
                    break;
                case "3G":
                    mNetworkType = 2;
                    break;
                case "4G":
                    mNetworkType = 3;
                    break;
            }
            MapItem offsetItem = new MapItem(lat, lng, mNetworkType);
            mClusterManager.addItem(offsetItem);
        }
       /* // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MapItem offsetItem = new MapItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }*/
    }
}


class MapItem implements ClusterItem {

    private final LatLng mPosition;
    private int mNetworkType = 0;

    public MapItem(double lat, double lng, int mNetworkType) {
        mPosition = new LatLng(lat, lng);
        this.mNetworkType = mNetworkType;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public int getNetworkType() {
        return mNetworkType;
    }
}

class OwnIconRendered extends DefaultClusterRenderer<MapItem> {

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MapItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapItem item, MarkerOptions markerOptions) {
        switch (item.getNetworkType()) {
            case 1:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                markerOptions.title("2G");
                break;
            case 2:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.title("3G");
                break;
            case 3:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markerOptions.title("4G");
                break;
            default:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markerOptions.title("Unknown");
        }
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        markerOptions.snippet(item.getSnippet());
//        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}