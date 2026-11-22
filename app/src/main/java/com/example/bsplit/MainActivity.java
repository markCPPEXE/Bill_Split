package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentUser = BillStorage.getCurrentUser(this);
        if (currentUser != null && !currentUser.trim().isEmpty()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        showLogin();
    }

    private void showLogin() {
        setContentView(R.layout.log_in);

        TextInputLayout usernameLayout = findViewById(R.id.username);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        TextInputEditText usernameInput = (TextInputEditText) usernameLayout.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) passwordLayout.getEditText();

        View loginBtn = findViewById(R.id.log_in_btn);
        View signupBtn = findViewById(R.id.signup);

        UIUtils.applyTouchScale(loginBtn);
        UIUtils.applyTouchScale(signupBtn);

        UIUtils.animateStaggered(50, 80,
                findViewById(R.id.header_card),
                findViewById(R.id.login_form_card),
                findViewById(R.id.dhaccount),
                signupBtn
        );

        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        loginBtn.setOnClickListener(v -> {
            String username = usernameInput == null || usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
            String password = passwordInput == null || passwordInput.getText() == null ? "" : passwordInput.getText().toString();

            if (usernameLayout != null) usernameLayout.setError(null);
            if (passwordLayout != null) passwordLayout.setError(null);

            if (username.isEmpty()) {
                if (usernameLayout != null) {
                    usernameLayout.setError("Enter your username");
                    UIUtils.shake(usernameLayout);
                }
                return;
            }
            if (password.isEmpty()) {
                if (passwordLayout != null) {
                    passwordLayout.setError("Enter your password");
                    UIUtils.shake(passwordLayout);
                }
                return;
            }

            if (BillStorage.login(this, username, password)) {
                BillStorage.setCurrentUser(this, username);
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else {
                UIUtils.shake(findViewById(R.id.login_form_card));
                UIUtils.showBanner(this, "Invalid username or password.", true);
            }
        });
    }
}
