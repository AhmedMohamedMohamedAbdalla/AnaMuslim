/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.ui.setup.wizard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.MaterialSharedAxis;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.objects.AlertAction;
import com.irozon.sneaker.Sneaker;
import com.typ.muslim.R;
import com.typ.muslim.app.Extras;
import com.typ.muslim.interfaces.GetLocationCallback;
import com.typ.muslim.managers.AMSettings;
import com.typ.muslim.managers.LocationManager;
import com.typ.muslim.managers.ResMan;
import com.typ.muslim.managers.ViewManager;
import com.typ.muslim.models.Location;
import com.typ.muslim.ui.setup.SelectLocationActivity;

public class LocationSelectorFragment extends Fragment {

    private final Handler handler = new Handler();
    // Runtime
    private LocationManager locationManager;
    private Location selectedLocation, gpsLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Enter and Exit Transitions
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
        // Setup LocationManager
        if (locationManager == null) locationManager = new LocationManager(requireActivity());
        // Get selected location from args if found
        if (getArguments() != null) {
            if (getArguments().containsKey(Extras.EXTRA_GPS_LOCATION)) gpsLocation = (Location) getArguments().getSerializable(Extras.EXTRA_GPS_LOCATION);
            if (getArguments().containsKey(Extras.EXTRA_SELECTED_LOCATION)) selectedLocation = (Location) getArguments().getSerializable(Extras.EXTRA_SELECTED_LOCATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(requireContext()).inflate(R.layout.fragment_wizard_loc_selector, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        // Setup Views
        final MaterialButton btnContinue = fragmentView.findViewById(R.id.btn_continue);
        final TextInputLayout tilGPSLocate = (TextInputLayout) fragmentView.findViewById(R.id.til_gps_location);
        final TextInputLayout tilManualLocate = (TextInputLayout) fragmentView.findViewById(R.id.til_select_location);
        assert tilManualLocate.getEditText() != null && tilGPSLocate.getEditText() != null : "Views are null";
        ActivityResultLauncher<Intent> selectLocationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Update runtime
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                final Location location = (Location) result.getData().getSerializableExtra(Extras.EXTRA_LOCATION);
                if (location == null) return; // Location is null
                selectedLocation = location; // Update runtime
                tilManualLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.black));
                tilManualLocate.getEditText().setText(String.format("%s, %s", location.getCityName(), location.getCountryName()));
            }
        });
        // Update views if location(s) set
        handler.post(getUpdateViewRunnable(tilManualLocate.getEditText(), tilGPSLocate));
        // Listeners
        boolean isGPSLocatingEnabled = false;
        tilManualLocate.getEditText().setOnClickListener(v -> selectLocationLauncher.launch(new Intent(requireContext(), SelectLocationActivity.class)));
        tilGPSLocate.getEditText().setOnClickListener(v -> {
            if (!isGPSLocatingEnabled) {
                Toast.makeText(getContext(), "GPS location is buggy now and not planned to be fixed until v1.1.10", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if gps location is already set
            if (gpsLocation != null) return; // Overloaded call
            // Show Loading
            tilGPSLocate.setEnabled(false);
            tilManualLocate.setEnabled(false);
            tilGPSLocate.getEditText().setText(R.string.locating_you);
            tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.btn_stroke_color));
            // Get location from GPS
            locationManager.getGPSLocation(new GetLocationCallback() {
                @Override
                public void onGetLocationSucceed(Location location) {
                    if (gpsLocation != null) return; // Overloaded call
                    // Update runtime and UI
                    gpsLocation = location;
                    tilGPSLocate.getEditText().setText(R.string.located);
                    tilGPSLocate.setEndIconDrawable(R.drawable.ic_location_located);
                    tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.green));
                    handler.postDelayed(getUpdateViewRunnable(tilManualLocate.getEditText(), tilGPSLocate), 1500);
                }

                @Override
                public void onPermissionDenied() {
                    tilGPSLocate.getEditText().setText(R.string.location_perm_denied);
                    tilGPSLocate.setEndIconDrawable(R.drawable.ic_location_disabled);
                    tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.red));
                    handler.postDelayed(getUpdateViewRunnable(tilManualLocate.getEditText(), tilGPSLocate), 3000);
                }

                @Override
                public void onGetLocationFailed() {
                    tilGPSLocate.setEndIconDrawable(R.drawable.ic_location_disabled);
                    tilGPSLocate.getEditText().setText(R.string.failed_to_detect_location);
                    tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.red));
                    handler.postDelayed(getUpdateViewRunnable(tilManualLocate.getEditText(), tilGPSLocate), 3000);
                }
            });
        });
        btnContinue.setOnClickListener(v -> {
            // Check if location was set
            if (selectedLocation == null && gpsLocation == null) {
                Sneaker.with(requireActivity()).setTitle(getString(R.string.select_location)).setMessage(getString(R.string.location_not_selected_error_msg)).setDuration(2500).sneakError();
                return;
            }
            // Show alert if user selected two locations
            if (selectedLocation != null && gpsLocation != null) {
                final AlertView alert = new AlertView(ResMan.getString(requireContext(), R.string.location_conflict), ResMan.getString(requireContext(), R.string.msg_pick_location_alert), AlertStyle.BOTTOM_SHEET);
                // Add use manual location action
                alert.addAction(new AlertAction(ResMan.getString(requireContext(), R.string.use_manual_location), AlertActionStyle.DEFAULT, alertAction -> {
                    previewLocation(selectedLocation);
                }));
                // Add use gps location action
                alert.addAction(new AlertAction(ResMan.getString(requireContext(), R.string.use_gps_location), AlertActionStyle.DEFAULT, alertAction -> {
                    previewLocation(gpsLocation);
                }));
                // Add dismiss action
                alert.addAction(new AlertAction(ResMan.getString(requireContext(), R.string.dismiss), AlertActionStyle.NEGATIVE, alertAction -> {
                }));
                alert.show((AppCompatActivity) requireActivity());
            } else previewLocation(selectedLocation != null ? selectedLocation : gpsLocation);
        });
    }

    private Runnable getUpdateViewRunnable(@NonNull EditText actManualLocate, TextInputLayout tilGPSLocate) {
        return () -> {
            if (!actManualLocate.isEnabled()) actManualLocate.setEnabled(true);
            if (selectedLocation != null) {
                // Found selected location
                actManualLocate.clearFocus();
                actManualLocate.setTextColor(ResMan.getColor(requireContext(), R.color.black));
                actManualLocate.setText(String.format("%s, %s", selectedLocation.getCityName(), selectedLocation.getCountryName()));
            } else {
                // No location selected
                actManualLocate.clearFocus();
                actManualLocate.setText(null);
                actManualLocate.setTextColor(ResMan.getColor(requireContext(), R.color.btn_stroke_color));
            }
            if (tilGPSLocate.getEditText() != null) {
                // GPS locations
                if (gpsLocation != null) {
                    // Found GPS location
                    tilGPSLocate.clearFocus();
                    tilGPSLocate.setEndIconDrawable(R.drawable.ic_location_located);
                    tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.black));
                    tilGPSLocate.getEditText().setText(String.format("%s, %s", gpsLocation.getCityName(), gpsLocation.getCountryName()));
                } else {
                    // No GPS locations found
                    tilGPSLocate.clearFocus();
                    tilGPSLocate.getEditText().setText(null);
                    tilGPSLocate.setEndIconDrawable(R.drawable.ic_location_searching);
                    tilGPSLocate.getEditText().setTextColor(ResMan.getColor(requireContext(), R.color.btn_stroke_color));
                }
            }
        };
    }

    private void previewLocation(@NonNull Location location) {
        new ViewManager.PreviewLocationBottomSheet(requireContext(), location, btn -> {
            if (btn.getId() == R.id.btn_continue) {
                // Save current location
                if (AMSettings.saveLocation(requireContext(), location)) {
                    // Navigate to next fragment
                    Navigation.findNavController(requireView()).navigate(R.id.action_locationSelectorFragment_to_selectPrayConfigFragment);
                } else Toast.makeText(requireContext(), "Can't save location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
