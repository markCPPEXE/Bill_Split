package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogin();
    }

    private void showLogin() {
        setContentView(R.layout.log_in);

        TextInputLayout usernameLayout = findViewById(R.id.username);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        TextInputEditText usernameInput = (TextInputEditText) usernameLayout.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) passwordLayout.getEditText();

        findViewById(R.id.signup).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SignUpActivity.class)));

        findViewById(R.id.log_in_btn).setOnClickListener(v -> {
            String username = usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
            String password = passwordInput.getText() == null ? "" : passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your username and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (BillStorage.login(this, username, password)) {
                BillStorage.setCurrentUser(this, username);
                // Credentials are correct: go straight to the home screen.
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish(); // so Back from Home doesn't return to the login screen
            } else {
                Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}