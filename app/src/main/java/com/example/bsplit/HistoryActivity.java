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
        adapter = new RecentBillAdapter(true);
        adapter.setOnBillActionListener((position, bill) -> {
            String username = BillStorage.getCurrentUser(this);
            BillStorage.deleteBill(this, username, position);
            refreshHistory();
        });
        recycler.setAdapter(adapter);

        UIUtils.animateStaggered(50, 60,
                findViewById(R.id.recent_bill),
                findViewById(R.id.txtHistorySubtitle)
        );

        BottomNav.setup(this, BottomNav.HISTORY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHistory();
    }

    private void refreshHistory() {
        String username = BillStorage.getCurrentUser(this);
        List<BillStorage.Bill> bills = BillStorage.getRecentBills(this, username, Integer.MAX_VALUE);
        adapter.setBills(bills);

        int count = bills.size();
        TextView subtitle = findViewById(R.id.txtHistorySubtitle);
        if (subtitle != null) {
            subtitle.setText(count + (count == 1 ? " bill split so far" : " bills split so far"));
        }

        boolean isEmpty = bills.isEmpty();
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
