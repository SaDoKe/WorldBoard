package org.sadoke.worldboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.sadoke.worldboard.sensormanager.SensorDataManager;
import org.sadoke.worldboard.ui.main.MainViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    private ImageView imgCompass;
    private TextView txtDegrees;
    private float currentDegree;
    private RESTApi api;
    private MainViewModel mainViewModel;
    private SensorDataManager sensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNeededPermissions();
        mainViewModel = new ViewModelProvider(
                this,
                new MainViewModel(this).getDefaultViewModelProviderFactory()
        ).get(MainViewModel.class);

        imgCompass = (ImageView) findViewById(R.id.imgCompass);
        txtDegrees = (TextView) findViewById(R.id.txtDegrees);
        sensorManager = new SensorDataManager(this, mainViewModel);
        api = RESTApi.init(this);

        mainViewModel.getDegree().observe(this, degree -> {
            txtDegrees.setText("Rotation: " + degree + " degrees");
            RotateAnimation ra = new RotateAnimation(currentDegree, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            currentDegree = degree;
            ra.setDuration(500);
            ra.setRepeatCount(0);
            ra.setFillAfter(true);
            imgCompass.startAnimation(ra);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.startLogging();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.stopLogging();
    }

    /**
     * Checks if all Permissions in Array are granted
     *
     * @return Boolean
     * True after check
     */
    private Boolean checkNeededPermissions() {
        List<String> neededPermissions;
        neededPermissions = Stream.of(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .filter(e -> ActivityCompat.checkSelfPermission(this, e) != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
                neededPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if (!neededPermissions.isEmpty())
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[]{}), 0);
        return true;
    }

    /**
     * Checks if all Permissions in an IntArray are granted
     *
     * @param grantedPermissions Array of Permissions
     * @return Boolean
     * True if all Permissions Granted, else False
     */
    private Boolean allPermissionsGranted(int[] grantedPermissions) {
        return Arrays.stream(grantedPermissions)
                .noneMatch(singleGrantedPermission -> singleGrantedPermission == PackageManager.PERMISSION_DENIED);
    }

    /**
     * Checks if All Permissions Granted on Runtime
     *
     * @param requestCode
     * @param permissions  All Permissions the App needs
     * @param grantResults Array of grant values from permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!allPermissionsGranted(grantResults))
            ActivityCompat.requestPermissions(this, permissions, 0);
    }
}