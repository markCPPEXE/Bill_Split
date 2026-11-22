package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        View loginBtn = findViewById(R.id.signup);
        View submitBtn = findViewById(R.id.log_in_btn);

        UIUtils.applyTouchScale(loginBtn);
        UIUtils.applyTouchScale(submitBtn);

        UIUtils.animateStaggered(50, 80,
                findViewById(R.id.header_card),
                findViewById(R.id.signup_form_card),
                findViewById(R.id.dhaccount),
                loginBtn
        );

        loginBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        TextInputLayout usernameLayout = findViewById(R.id.username);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        TextInputLayout confirmLayout = findViewById(R.id.confirm_password);

        TextInputEditText usernameInput = (TextInputEditText) usernameLayout.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) passwordLayout.getEditText();
        TextInputEditText confirmInput = (TextInputEditText) confirmLayout.getEditText();

        submitBtn.setOnClickListener(v -> {
            String username = usernameInput == null || usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
            String password = passwordInput == null || passwordInput.getText() == null ? "" : passwordInput.getText().toString();
            String confirm = confirmInput == null || confirmInput.getText() == null ? "" : confirmInput.getText().toString();

            if (usernameLayout != null) usernameLayout.setError(null);
            if (passwordLayout != null) passwordLayout.setError(null);
            if (confirmLayout != null) confirmLayout.setError(null);

            if (username.isEmpty()) {
                if (usernameLayout != null) {
                    usernameLayout.setError("Enter a username");
                    UIUtils.shake(usernameLayout);
                }
                return;
            }
            if (password.isEmpty()) {
                if (passwordLayout != null) {
                    passwordLayout.setError("Enter a password");
                    UIUtils.shake(passwordLayout);
                }
                return;
            }
            if (password.length() < 6) {
                if (passwordLayout != null) {
                    passwordLayout.setError("Password must be at least 6 characters");
                    UIUtils.shake(passwordLayout);
                }
                return;
            }
            if (!password.equals(confirm)) {
                if (confirmLayout != null) {
                    confirmLayout.setError("Passwords do not match");
                    UIUtils.shake(confirmLayout);
                }
                return;
            }

            if (!BillStorage.register(this, username, password)) {
                if (usernameLayout != null) {
                    usernameLayout.setError("Username already exists");
                    UIUtils.shake(usernameLayout);
                }
                UIUtils.showBanner(this, "Username already exists.", true);
            } else {
                BillStorage.setCurrentUser(this, username);
                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
}
