package com.knowledgeplanet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.knowledgeplanet.android.adapter.SubCourseAdapter;
import com.knowledgeplanet.android.model.Courses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 26-08-2017.
 */

public class SubCourseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private List<Courses> courseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubCourseAdapter mAdapter;
    private String courseName;
    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
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

        courseName = getIntent().getStringExtra("courseName");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new SubCourseAdapter(courseList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Courses courses = courseList.get(position);
                Toast.makeText(getApplicationContext(), courses.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareCourseData(courseName);
    }

    private void prepareCourseData(String courseName) {
        if(courseName.equalsIgnoreCase("Computer")){
            Courses courses = new Courses("Fundamental");
            courseList.add(courses);

            courses = new Courses("DBMS");
            courseList.add(courses);

            courses = new Courses("ICT");
            courseList.add(courses);

            courses = new Courses("Computer Network");
            courseList.add(courses);

            courses = new Courses("Software Engineering");
            courseList.add(courses);
        }else{
            Courses courses = new Courses("B.Ed");
            courseList.add(courses);

            /*courses = new Courses("Education");
            courseList.add(courses);*/
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void goToLoginActivity(FirebaseUser user) {
        Intent mainIntent = new Intent(SubCourseActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}