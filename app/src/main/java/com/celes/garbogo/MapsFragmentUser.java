package com.celes.garbogo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragmentUser extends Fragment{
    View view;
    GoogleMap mMap;
    Marker marker;
    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment supportMapFragment;

    LocationSendActivity locationSendActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            locationSendActivity = (LocationSendActivity) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps_user, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getLastLocation();

        return view;
    }
    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    implementLocation(addresses);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
        }else{
            askPermission();
        }
    }

    private void implementLocation(List<Address> addresses) {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;;
                LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here");
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                locationSendActivity.locationSend(addresses.get(0).getAddressLine(0), String.valueOf(addresses.get(0).getLatitude())
                        ,String.valueOf(addresses.get(0).getLongitude()));


                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if(marker!=null) marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Here"));
                        Geocoder geocoder1 = new Geocoder(getContext(), Locale.getDefault());

                        try {
                            List<Address> addresses1 = geocoder1.getFromLocation(latLng.latitude,latLng.longitude,1);
                            locationSendActivity.locationSend(addresses1.get(0).getAddressLine(0), String.valueOf(addresses1.get(0).getLatitude())
                                    ,String.valueOf(addresses1.get(0).getLongitude()));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
    private void askPermission() {
        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result){
                            getLastLocation();
                        }
                        else{
                            Toast.makeText(getActivity(), "Please Grant Permission", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}