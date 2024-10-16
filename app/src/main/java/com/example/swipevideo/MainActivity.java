package com.example.swipevideo;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity for the swipe video app, responsible for initializing Firebase and setting up the UI.
 */
public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference; // Reference to Firebase database
    FirebaseDatabase firebaseDatabase; // Firebase database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_main);

        // Apply system window insets to the view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Call method to read data from Firebase
        readData();
    }

    /**
     * Reads video data from Firebase and updates the ViewPager with the fetched videos.
     */
    private void readData() {
        // Get a reference to the "videos" node in Firebase
        DatabaseReference videoReference = firebaseDatabase.getReference("videos");

        // Listen for changes in the "videos" node
        videoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<VideoItem> videoItemsList = new ArrayList<>();

                // Check if data exists in the snapshot
                if (snapshot.exists()) {
                    // Iterate through each child node (video) in the snapshot
                    for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
                        VideoItem videoItem = new VideoItem();
                        videoItem.videoTitle = videoSnapshot.child("title").getValue(String.class);
                        videoItem.videoDescription = videoSnapshot.child("description").getValue(String.class);
                        videoItem.videoURL = videoSnapshot.child("url").getValue(String.class);
                        videoItem.videoID = videoSnapshot.child("videoID").getValue(String.class);

                        // Add the video item to the list
                        videoItemsList.add(videoItem);
                    }

                    // Update the ViewPager adapter with the fetched video items
                    ViewPager2 videoViewPager = findViewById(R.id.videosViewPager);
                    videoViewPager.setAdapter(new VideoAdapter(videoItemsList));
                } else {
                    // Log an error if no data was found in Firebase
                    Log.e("FirebaseData", "No data found in the database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error message if data fetching is cancelled
                Log.e("FirebaseDataError", error.getMessage());
            }
        });
    }
}
