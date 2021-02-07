package com.tommy.tabsinfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private ProgressBar barSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        barSignIn = findViewById(R.id.barSignIn);

        Sprite doubleBounce = new DoubleBounce();
        barSignIn.setIndeterminateDrawable(doubleBounce);

        findViewById(R.id.btnSignIn).setOnClickListener(view -> signInEmailPasswordAccount());
    }

    private void signInEmailPasswordAccount() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty()) {
            inputEmail.setError("Email is required");
            inputEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email is not valid");
            inputEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            inputPassword.requestFocus();
            return;
        }

        barSignIn.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        barSignIn.setVisibility(View.GONE);
                        updateUI(mAuth.getCurrentUser());

                    } else {
                        barSignIn.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {

        if(currentUser != null){

            finish();

            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
    }
}