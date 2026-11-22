package com.example.bsplit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecentBillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        // RecyclerView setup (once)
        RecyclerView recycler = findViewById(R.id.recentBillsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentBillAdapter();
        recycler.setAdapter(adapter);

        BottomNav.setup(this, BottomNav.HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the numbers/bills when returning from another screen.
        showDashboard();
    }

    private void showDashboard() {
        TextView name = findViewById(R.id.txtAccountName);
        TextView totalSpent = findViewById(R.id.txtTotalSpent);
        TextView billCount = findViewById(R.id.txtBillSplitted);
        TextView emptyText = findViewById(R.id.txtEmptyBills);
        RecyclerView recycler = findViewById(R.id.recentBillsRecycler);

        String username = BillStorage.getCurrentUser(this);
        name.setText(username.isEmpty() ? "Guest" : username);

        Peso.setText(totalSpent, Peso.format(BillStorage.getTotalSpent(this, username)));
        billCount.setText(String.valueOf(BillStorage.getBillCount(this, username)));

        // 3 latest bills only
        List<BillStorage.Bill> recentBills = BillStorage.getRecentBills(this, username, 3);
        adapter.setBills(recentBills);

        boolean isEmpty = recentBills.isEmpty();
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}