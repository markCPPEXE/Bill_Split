package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        View backBtn = findViewById(R.id.btnBack);
        View logoutBtn = findViewById(R.id.btnLogout);

        if (backBtn != null) {
            UIUtils.applyTouchScale(backBtn);
            backBtn.setOnClickListener(v -> finish());
        }
        if (logoutBtn != null) {
            UIUtils.applyTouchScale(logoutBtn);
            logoutBtn.setOnClickListener(v -> confirmLogout());
        }

        UIUtils.applyTouchScale(findViewById(R.id.avatarCard));
        UIUtils.applyTouchScale(findViewById(R.id.billsSplitCard));
        UIUtils.applyTouchScale(findViewById(R.id.amountSpentCard));
        UIUtils.applyTouchScale(findViewById(R.id.accountDetailsCard));

        UIUtils.animateStaggered(50, 50,
                findViewById(R.id.avatarCard),
                txtProfileName,
                findViewById(R.id.memberBadge),
                findViewById(R.id.billsSplitCard),
                findViewById(R.id.amountSpentCard),
                findViewById(R.id.accountDetailsCard),
                logoutBtn
        );

        BottomNav.setup(this, BottomNav.PROFILE);
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
        UIUtils.showConfirmDialog(
                this,
                R.drawable.ic_logout,
                0xFFFEE2E2, // soft red badge
                0xFFDC2626, // red icon
                "Log Out?",
                "Are you sure you want to log out of your account?",
                "Log Out",
                0xFFDC2626, // red confirm button
                this::logout
        );
    }

    private void logout() {
        BillStorage.setCurrentUser(this, "");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
