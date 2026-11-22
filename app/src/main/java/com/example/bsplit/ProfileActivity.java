package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtAvatar, txtProfileName, txtAccountName, txtBillSplitted, txtAmountSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        txtAvatar = findViewById(R.id.txtAvatar);
        txtProfileName = findViewById(R.id.txtProfileName);
        txtAccountName = findViewById(R.id.txtAccountName);
        txtBillSplitted = findViewById(R.id.txtBillSplitted);
        txtAmountSpent = findViewById(R.id.txtAmountSpent);

        // Profile is not a tab, so no bottom icon is highlighted here.
        BottomNav.setup(this, BottomNav.PROFILE);

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProfile();
    }

    private void showProfile() {
        String username = BillStorage.getCurrentUser(this);
        String name = username.isEmpty() ? "Guest" : username;

        txtAvatar.setText(name.substring(0, 1).toUpperCase(Locale.ROOT));
        txtProfileName.setText(name);
        txtAccountName.setText(name);
        txtBillSplitted.setText(String.valueOf(BillStorage.getBillCount(this, username)));
        Peso.setText(txtAmountSpent, Peso.format(BillStorage.getTotalSpent(this, username)));
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Log out", (dialog, which) -> logout())
                .show();
    }

    private void logout() {
        BillStorage.setCurrentUser(this, "");

        // Clear the whole back stack so Back can't return to a logged-in screen.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}