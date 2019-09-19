package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.location.LocationManager.GPS_PROVIDER;


public class DiseaseDetailsActivity extends AppCompatActivity {

    private LocationListener mLocationListener;
    private LocationManager locationManager;
    private String diseaseName;
    private String plantName;
    private String diseasePredictionAccuracy;

    private TextView diseaseNameTextView;
    private TextView plantNameTextView;
    private TextView diseasePredictionAccuracyTextView;
    private TextView currentWeatherCityNameTextView;
    private TextView currentWeatherTempTextView;
    private TextView currentWeatherHumTextView;
    private ImageView currentWeatherIcon;
    private Button searchForMore;
    private ProgressBar predictionAccuracyProgressBar;

    private float longitude=45.5017f, latitude=-73.5673f;

    private OpenWeatherMapHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        checkLocationPermission();

        helper = new OpenWeatherMapHelper("fe0009440b0183427a12a11de6265c14");
        helper.setLang(Lang.ENGLISH);
        helper.setUnits(Units.METRIC);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                Log.d("GPS _____", latitude +" "+ longitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.getLastKnownLocation(GPS_PROVIDER);

        getCurrentWeather();
        diseaseName = getIntent().getStringExtra("DISEASE_NAME");
        plantName = getIntent().getStringExtra("PLANT_NAME");
        diseasePredictionAccuracy = getIntent().getStringExtra("DISEASE_PREDICTION_ACCURACY");

        diseaseNameTextView = findViewById(R.id.diseaseName);
        plantNameTextView = findViewById(R.id.plantName);
        diseasePredictionAccuracyTextView = findViewById(R.id.predictionAccuracyPercent);
        predictionAccuracyProgressBar = findViewById(R.id.predictionAccuracyProgressBar);
        searchForMore = findViewById(R.id.searchForMore);

        currentWeatherCityNameTextView = findViewById(R.id.currentWeatherCityName);
        currentWeatherTempTextView = findViewById(R.id.currentWeatherTemp);
        currentWeatherIcon = findViewById(R.id.currentWeatherIcon);
        currentWeatherHumTextView = findViewById(R.id.currentWeatherHumidity);

        diseaseNameTextView.setText(diseaseName);
        plantNameTextView.setText(plantName);
        diseasePredictionAccuracyTextView.setText(diseasePredictionAccuracy);

        int progressState = Math.round(Float.parseFloat(diseasePredictionAccuracy.replace("%", "")));
        predictionAccuracyProgressBar.setProgress(progressState);

        searchForMore.setOnClickListener(view -> {
            startIntentGoogleSearchView(plantName+" "+diseaseName+" treatments");
        });
    }

    private void startIntentGoogleSearchView(String query) {
        String escapedQuery = null;
        try {
            escapedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    private void getCurrentWeather() {
        helper.getCurrentWeatherByGeoCoordinates(longitude, latitude, new  CurrentWeatherCallback() {

            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                String url = "https://openweathermap.org/img/wn/"+currentWeather.getWeather().get(0).getIcon()+"@2x.png";
                Glide.with(DiseaseDetailsActivity.this).load(url).into(currentWeatherIcon);
                currentWeatherCityNameTextView.setText(currentWeather.getName());
                currentWeatherTempTextView.setText(currentWeather.getMain().getTemp()+"C°");
                currentWeatherHumTextView.setText(currentWeather.getMain().getHumidity()+"%");
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private Bitmap urlToBitmap(String icon) {
        try {
            URL url = new URL("http://openweathermap.org/img/wn/"+icon+"2x.png");
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
