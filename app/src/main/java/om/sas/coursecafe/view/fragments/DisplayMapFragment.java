package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.CoursesModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayMapFragment extends Fragment implements OnMapReadyCallback {

    private OnDisplayMapFragmentInteractionListener mListener;
    private static final String KEY_POST = "param1";
    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    private Marker selectedLocation;
    private double mlat;
    private double mlng;



    private CoursesModel mCourseModel;
    private String mAddressLocation;


    public DisplayMapFragment() {
        // Required empty public constructor
    }



    public static DisplayMapFragment newInstance(CoursesModel courseModel) {
        DisplayMapFragment fragment = new DisplayMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_POST, courseModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseModel = (CoursesModel) getArguments().getSerializable(KEY_POST);

        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.displayLocationTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);

        }
        readLocationFromFirebase();
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_display_map, container, false);

        mMapView = parentView.findViewById(R.id.display_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        return parentView;
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

//        double latitude =23.58661153007602;
//        double longitude=58.16779553890228;
        //Toast.makeText(mContext, "data read from FB"+mlng, Toast.LENGTH_SHORT).show();
        // Add a marker in Alsib and move the camera to KOM 4
        //LatLng KOM4 = new LatLng(23.5673, 58.1725);// select the location
        LatLng location = new LatLng(mlat,mlng);// select the location
        mMap.addMarker(new MarkerOptions().position(location).title(mAddressLocation));// add marker non the location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location)); // move the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.5f), 1000, null);  // Zoom in, animating the camera.
    }

    private void readLocationFromFirebase() {
        // read mlat, mlng from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefPost = database.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourseModel.getPostId());

        myRefPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CoursesModel value = dataSnapshot.getValue(CoursesModel.class);


                mlat=value.getLocation_lat();
                mlng=value.getLocation_lng();
                mAddressLocation=value.getLocationAddress();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnDisplayMapFragmentInteractionListener) {
            mListener = (OnDisplayMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayMapFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnDisplayMapFragmentInteractionListener {
        void onFragmentInteraction(String title);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }


}
