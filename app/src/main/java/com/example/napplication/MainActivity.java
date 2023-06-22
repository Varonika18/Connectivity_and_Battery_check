package com.example.napplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Handler mHandler;
    private Runnable mRunnable;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1002;

 ImageView imageView;
    TextView textcharge,Connection,locationadd,charging,counterTextView,dateTimeTextView,textView;
    private Button captureButton;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int counter = 0;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float)scale;
            textcharge.setText(String.valueOf(batteryPct) + "%");
        }
    };
    private BroadcastReceiver cBatInfoReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if(isCharging){
                charging.setText("ON");
            }
            else {
                charging.setText("OFF");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.textView);
        // Create a SpannableStringBuilder
        String Text="SECQURAISE";
        SpannableString spannableString = new SpannableString(Text);

        // Apply different color and size to specific parts of the text
        int color1 = Color.parseColor("#235854");
        float size1 = 0.7f;
        spannableString.setSpan(new ForegroundColorSpan(color1), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(size1), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color2 = Color.parseColor("#235854");
        float size2 = 0.5f;
        spannableString.setSpan(new ForegroundColorSpan(color2), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(size2), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color3 = Color.RED;
        float size3 = 0.7f;
        spannableString.setSpan(new ForegroundColorSpan(color3), 6, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(size3), 6, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color4 = Color.parseColor("#235854");
        float size4 = 0.5f;
        spannableString.setSpan(new ForegroundColorSpan(color4), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(size4), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString to the TextView
        textView.setText(spannableString);;


        firebaseStorage = FirebaseStorage.getInstance("gs://varonikarai-17d55.appspot.com");
        storageReference = firebaseStorage.getReference();
        firebaseDatabase=FirebaseDatabase.getInstance("https://varonikarai-17d55-default-rtdb.firebaseio.com");
        databaseReference=firebaseDatabase.getReference();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, start listening for location updates
            startLocationUpdates();
        }
        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {

                mHandler.postDelayed(this, 900000);
            }
        };

        // Start the initial auto refresh
        mHandler.postDelayed(mRunnable, 5000);
       textcharge=this.findViewById(R.id.charge);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
       captureButton=findViewById(R.id.refreshbtn);
        dateTimeTextView=findViewById(R.id.datetime);
       counterTextView=findViewById(R.id.count);
       imageView=findViewById(R.id.imageView);
       Connection=findViewById(R.id.connectivity);
       Connection.setText(isConnected());
       charging=findViewById(R.id.charging);
       this.registerReceiver(this.cBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
       locationadd=findViewById(R.id.location);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    imageView.setBackground(null);
                    imageView.setImageBitmap(bitmap);
                    uploadImageToFirebase(bitmap);
                }
            }
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String imageFileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child("images/" + imageFileName);

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                counter++;
                counterTextView.setText(String.valueOf(counter));
                String imageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                storeImageDataInDatabase(imageUrl);
                Toast.makeText(MainActivity.this, "Image uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error uploading image
                Log.e("Firebase", "Failed to upload image to Firebase Storage: " + e.getMessage());
            }
        });
    }
    private void storeImageDataInDatabase(String imageUrl) {
        // Get current time and date
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create a unique key for the image data
        String imageDataId = databaseReference.child("imageData").push().getKey();

        // Create a map to store the image data
        dateTimeTextView.setText(currentDate+" "+currentTime);
        ImageData imageData = new ImageData(imageUrl, currentTime, currentDate);
        databaseReference.child("imageData").child(imageDataId).setValue(imageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image data stored successfully
                        Toast.makeText(MainActivity.this, "Image data stored in Firebase Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error storing image data
                        Log.e("Firebase", "Failed to store image data in Firebase Database: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(MainActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // Use GPS as the location provider
                    0, 0, // Minimum time interval between updates (0 milliseconds)
                    this); // LocationListener
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    String isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
         if(connected){
             return  "ON";
         }
         return "OFF";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mRunnable);
        locationManager.removeUpdates(this);
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        locationadd.setText(latitude + " " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

}
