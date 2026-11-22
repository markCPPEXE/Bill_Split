package com.example.bsplit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        findViewById(R.id.signup).setOnClickListener(v -> finish());

        TextInputLayout usernameLayout = findViewById(R.id.username);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        TextInputLayout confirmLayout = findViewById(R.id.confirm_password);

        TextInputEditText usernameInput = (TextInputEditText) usernameLayout.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) passwordLayout.getEditText();
        TextInputEditText confirmInput = (TextInputEditText) confirmLayout.getEditText();

        findViewById(R.id.log_in_btn).setOnClickListener(v -> {
            String username = usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
            String password = passwordInput.getText() == null ? "" : passwordInput.getText().toString();
            String confirm = confirmInput.getText() == null ? "" : confirmInput.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else if (!BillStorage.register(this, username, password)) {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
            } else {
                BillStorage.setCurrentUser(this, username);
                Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
