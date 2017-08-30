package com.knowledgeplanet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnGoogleLogin = (Button) findViewById(R.id.btn_google_login);
        btnGoogleLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_google_login:
                goToMainActivity();
                break;
        }
    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
