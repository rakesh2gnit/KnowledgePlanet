package com.knowledgeplanet.android;

import android.*;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.knowledgeplanet.android.adapter.CourseAdapter;
import com.knowledgeplanet.android.adapter.SubjectAdapter;
import com.knowledgeplanet.android.model.Course;

import java.io.File;

/**
 * Created by Admin on 11-09-2017.
 */

public class SubjectActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "CourseActivity";
    private RecyclerView recyclerView;
    private DatabaseReference mCourseDb;
    private SubjectAdapter mAdapter;
    private FloatingActionButton fabLogout;
    private FirebaseAuth mAuth;
    private String courseName;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        courseName = getIntent().getStringExtra("courseName");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mCourseDb = FirebaseDatabase.getInstance().getReference()
                .child(courseName);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fabLogout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fabLogout.setOnClickListener(this);

        //mAdapter = new CourseAdapter(courseList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Courses courses = courseList.get(position);
                final TextView imageName = (TextView) view.findViewById(R.id.genre);
                //Toast.makeText(SubjectActivity.this, "Single Click on position        :" + position + " , " + txtCourse.getText().toString().trim(),
                //Toast.LENGTH_SHORT).show();
                if (imageName.getText().toString().trim().length() != 0) {
                    downloadFile(imageName.getText().toString().trim());
                } else {
                    Toast.makeText(SubjectActivity.this, "No File Attach", Toast.LENGTH_SHORT).show();
                }
                //showDialog(txtCourse.getText().toString().trim());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener courseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Course object and use the values to update the UI
                Log.e("count", "" + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() == 0) {
                    ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_empty)).setText("No Subject Found");
                } else {
                    ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.GONE);
                    // Listen for comments
                    mAdapter = new SubjectAdapter(SubjectActivity.this, mCourseDb);
                    recyclerView.setAdapter(mAdapter);
                }
                //Course post = dataSnapshot.getValue(Course.class);
                //Log.e("Inside Start","onDataChange:"+post.Course);
                // [START_EXCLUDE]
                //mAuthorView.setText(post.author);
                //mTitleView.setText(post.title);
                //mBodyView.setText(post.body);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Course failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(SubjectActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mCourseDb.addValueEventListener(courseListener);
    }

    private void showDialog(final String course) {
        mCourseDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("dataSnapshot", dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(SubjectActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("What do you want to do?")
                .setTitle("Alert");

        // Add the buttons
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mCourseDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Course deleteSD = snapshot.getValue(Course.class);
                            if (course.equalsIgnoreCase(deleteSD.course)) {
                                mCourseDb.child(snapshot.getKey().toString()).removeValue();
                                break;
                            }
                        }
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //mCourseDb.child("-KteiDr2Isb9ftP1iYPT").removeValue();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up comments listener
        if (mAdapter != null)
            mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_logout:
                signOut();
                break;
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        goToLoginActivity(null);
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void goToLoginActivity(FirebaseUser user) {
        Intent mainIntent = new Intent(SubjectActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void downloadFile(String fileName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://knowledgeplanetadmin-178916.appspot.com");
        StorageReference islandRef = storageRef.child(fileName);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Knowledge Planet");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, fileName);

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
                String extension = localFile.getAbsolutePath().substring(localFile.getAbsolutePath().lastIndexOf("."));
                Log.e(TAG, "extn:" + extension);
                if (extension.equalsIgnoreCase(".pdf")) {
                    openPdf(localFile);
                } else {
                    openImage(localFile);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
    }

    private void openPdf(File file) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    private void openImage(File file) {
        Log.e(TAG, "Inside open image");
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image*//*");
        startActivity(intent);*/
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.getAbsolutePath());
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}