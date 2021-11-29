package om.sas.coursecafe.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import om.sas.coursecafe.R;


public class PickMapFragment extends Fragment implements OnMapReadyCallback {

    private int PERMISSION_ID = 44;

    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    private Marker selectedLocation;
    private double mlat;
    private double mlng;
    private List<Address> addressees;
    private String countryName;
    private OnPickLocationFragmentInteractionListener mListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private SearchView searchView;


    public PickMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.selectLocationTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_pick_map, container, false);

        mMapView = parentView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        searchView =parentView.findViewById(R.id.sv_pick_location);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location =searchView.getQuery().toString();
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(mContext);
                try{
                    addressList = geocoder.getFromLocationName(location,4);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Address address =  addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mMapView.getMapAsync(PickMapFragment.this);
                return false;
            }
        });

        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(this);

        Button btnSaveLocation = parentView.findViewById(R.id.btn_save);
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onAddLocationPressed(countryName, mlat, mlng);
                //Toast.makeText(mContext, mlat + " | " + mlng+ " | " +countryName, Toast.LENGTH_SHORT).show();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLastLocation();


        return parentView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mlat = 23.5673;
        mlng = 58.1725;




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                mlat = latLng.latitude;
                mlng = latLng.longitude;
                getLocationName();

                mMap.clear();

                selectedLocation = mMap.addMarker(new MarkerOptions().position(latLng).title(countryName));


            }
        });

    }

    private void createLocationPen() {
        mMap.clear();
        // Add a marker in Alsib and move the camera to KOM 4
        LatLng location = new LatLng(mlat, mlng);// select the location
        Log.d("location", countryName);
        selectedLocation = mMap.addMarker(new MarkerOptions().position(location).title(countryName));// add marker non the location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location)); // move the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.5f), 1000, null);  // Zoom in, animating the camera.
    }


    private void getLocationName() {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addressees = geocoder.getFromLocation(mlat, mlng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        countryName = addressees.get(0).getAddressLine(0);


    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    mlat = location.getLatitude();
                                    mlng = location.getLongitude();
                                    getLocationName();
                                    createLocationPen();
                                }
                            }
                        }
                );
            } else {

                Toast.makeText(mContext, Objects.requireNonNull(getActivity()).getString(R.string.on_location), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mlat = mLastLocation.getLatitude();
            mlng = mLastLocation.getLongitude();
            getLocationName();
            createLocationPen();

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    public void onAddLocationPressed(String mFullAddress, double locationLat, double locationLng) {
        if (mListener != null) {
            mListener.onAddLocation(mFullAddress,locationLat, locationLng);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof PickMapFragment.OnPickLocationFragmentInteractionListener) {
            mListener = (PickMapFragment.OnPickLocationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPickLocationFragmentInteractionListener {

        void onAddLocation(String mFullAddress, double locationLat, double locationLng);
        void onFragmentInteraction(String title);
        void hideBottomNavBar();
        void showBackButton(boolean show);

    }

}
