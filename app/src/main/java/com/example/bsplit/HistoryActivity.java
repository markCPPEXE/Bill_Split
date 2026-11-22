package com.example.bsplit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Shows every saved bill (Home only shows the latest 3). */
public class HistoryActivity extends AppCompatActivity {

    private RecentBillAdapter adapter;
    private RecyclerView recycler;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_bill);

        recycler = findViewById(R.id.historyRecycler);
        emptyText = findViewById(R.id.txtEmptyHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentBillAdapter();
        recycler.setAdapter(adapter);

        BottomNav.setup(this, BottomNav.HISTORY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = BillStorage.getCurrentUser(this);
        List<BillStorage.Bill> bills = BillStorage.getRecentBills(this, username, Integer.MAX_VALUE);
        adapter.setBills(bills);

        boolean isEmpty = bills.isEmpty();
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}