package com.knowledgeplanet.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgeplanet.android.adapter.CourseAdapter;
import com.knowledgeplanet.android.model.Course;

/**
 * Created by Admin on 11-09-2017.
 */

public class CourseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "CourseActivity";
    private RecyclerView recyclerView;
    private DatabaseReference mCourseDb;
    private CourseAdapter mAdapter;
    private FloatingActionButton fabLogout;
    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                .child("course");

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
                final TextView txtCourse = (TextView) view.findViewById(R.id.course);
                Toast.makeText(CourseActivity.this, "Single Click on position        :" + position + " , " + txtCourse.getText().toString().trim(),
                        Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(CourseActivity.this, SubjectActivity.class);
                mainIntent.putExtra("courseName",txtCourse.getText().toString().trim());
                startActivity(mainIntent);
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
                } else {
                    ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.GONE);
                    // Listen for comments
                    mAdapter = new CourseAdapter(CourseActivity.this, mCourseDb);
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
                Toast.makeText(CourseActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mCourseDb.addValueEventListener(courseListener);
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
        switch (view.getId()){
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
        Intent mainIntent = new Intent(CourseActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
