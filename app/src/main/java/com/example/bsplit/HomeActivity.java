package com.example.bsplit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    // Number of latest bills shown on the Home tab
    private static final int RECENT_BILLS_LIMIT = 4;

    private View containerHome, containerSplit, containerHistory;
    private RecentBillAdapter recentAdapter, historyAdapter;
    private RecyclerView recentRecyclerView;
    private androidx.core.widget.NestedScrollView homeScroll;

    // Split form views
    private TextInputLayout tilBiller, tilSplitPeople, tilTotalAmount;
    private EditText edtBiller, edtSplitPeople, edtTotalAmount;
    private TextView txtSplitAmount;
    private String lastSplitText = "";

    // Bottom Navigation Icons & Labels
    private ImageView imgNavHome, imgNavSplit, imgNavHistory;
    private TextView txtNavHome, txtNavHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                UIUtils.showConfirmDialog(
                        HomeActivity.this,
                        R.drawable.ic_exit,
                        0xFFE0E7FF,
                        0xFF2962FF,
                        "Exit App?",
                        "Are you sure you want to exit Bsplit?",
                        "Exit",
                        0xFF2962FF,
                        () -> finishAffinity()
                );
            }
        });

        containerHome = findViewById(R.id.containerHome);
        containerSplit = findViewById(R.id.containerSplit);
        containerHistory = findViewById(R.id.containerHistory);

        View imgProfile = findViewById(R.id.imgProfile);
        if (imgProfile != null) {
            UIUtils.applyTouchScale(imgProfile);
            imgProfile.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View heroCard = findViewById(R.id.heroCard);
        View btnSplitBill = findViewById(R.id.btnSplitBill);
        View seeAll = findViewById(R.id.txtSeeAll);

        if (heroCard != null) UIUtils.applyTouchScale(heroCard);
        if (btnSplitBill != null) {
            UIUtils.applyTouchScale(btnSplitBill);
            btnSplitBill.setOnClickListener(v -> showTab(BottomNav.SPLIT));
        }
        if (seeAll != null) {
            UIUtils.applyTouchScale(seeAll);
            seeAll.setOnClickListener(v -> showTab(BottomNav.HISTORY));
        }

        tilBiller = findViewById(R.id.tilBiller);
        tilSplitPeople = findViewById(R.id.tilSplitPeople);
        tilTotalAmount = findViewById(R.id.tilTotalAmount);

        edtBiller = findViewById(R.id.edtBiller);
        edtSplitPeople = findViewById(R.id.edtSplitPeople);
        edtTotalAmount = findViewById(R.id.edtTotalAmount);
        txtSplitAmount = findViewById(R.id.txtSplitAmount);
        View btnSplit = findViewById(R.id.btnSplit);

        if (btnSplit != null) {
            UIUtils.applyTouchScale(btnSplit);
            btnSplit.setOnClickListener(v -> saveBill());
        }

        UIUtils.applyTouchScale(findViewById(R.id.splitResultCard));

        TextWatcher liveUpdate = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updatePreview(); }
        };
        if (edtSplitPeople != null) edtSplitPeople.addTextChangedListener(liveUpdate);
        if (edtTotalAmount != null) edtTotalAmount.addTextChangedListener(liveUpdate);

        if (edtTotalAmount != null) {
            edtTotalAmount.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveBill();
                    return true;
                }
                return false;
            });
        }

        RecyclerView recentRecycler = findViewById(R.id.recentBillsRecycler);
        if (recentRecycler != null) {
            recentRecycler.setLayoutManager(new LinearLayoutManager(this));
            recentAdapter = new RecentBillAdapter(false);
            recentAdapter.setOnBillActionListener((position, bill) -> {
                String username = BillStorage.getCurrentUser(this);
                BillStorage.deleteBill(this, username, position);
                showDashboard();
            });
            recentRecycler.setAdapter(recentAdapter);
            recentRecyclerView = recentRecycler;
        }

        homeScroll = findViewById(R.id.homeScroll);
        if (homeScroll != null) {
            homeScroll.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
                if (b - t != ob - ot) fitRecentBillsToScreen();
            });
        }

        RecyclerView historyRecycler = findViewById(R.id.historyRecycler);
        if (historyRecycler != null) {
            historyRecycler.setLayoutManager(new LinearLayoutManager(this));
            historyAdapter = new RecentBillAdapter(true);
            historyAdapter.setOnBillActionListener((position, bill) -> {
                String username = BillStorage.getCurrentUser(this);
                BillStorage.deleteBill(this, username, position);
                showDashboard();
            });
            historyRecycler.setAdapter(historyAdapter);
        }

        View navHome = findViewById(R.id.navHome);
        View navSplit = findViewById(R.id.navSplit);
        View navHistory = findViewById(R.id.navHistory);

        imgNavHome = findViewById(R.id.imgNavHome);
        imgNavSplit = findViewById(R.id.imgNavSplit);
        imgNavHistory = findViewById(R.id.imgNavHistory);
        txtNavHome = findViewById(R.id.txtNavHome);
        txtNavHistory = findViewById(R.id.txtNavHistory);

        if (navHome != null) {
            UIUtils.applyTouchScale(navHome);
            navHome.setOnClickListener(v -> showTab(BottomNav.HOME));
        }
        if (navSplit != null) {
            UIUtils.applyTouchScale(navSplit);
            navSplit.setOnClickListener(v -> showTab(BottomNav.SPLIT));
        }
        if (navHistory != null) {
            UIUtils.applyTouchScale(navHistory);
            navHistory.setOnClickListener(v -> showTab(BottomNav.HISTORY));
        }

        showTab(BottomNav.HOME);
        updatePreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDashboard();
    }

    private void showTab(int tab) {
        if (containerHome != null) containerHome.setVisibility(tab == BottomNav.HOME ? View.VISIBLE : View.GONE);
        if (containerSplit != null) containerSplit.setVisibility(tab == BottomNav.SPLIT ? View.VISIBLE : View.GONE);
        if (containerHistory != null) containerHistory.setVisibility(tab == BottomNav.HISTORY ? View.VISIBLE : View.GONE);

        if (imgNavHome != null) {
            imgNavHome.setImageResource(tab == BottomNav.HOME ? R.drawable.icons8_home_50_blue : R.drawable.icons8_home_50);
        }
        if (txtNavHome != null) {
            txtNavHome.setTextColor(tab == BottomNav.HOME ? 0xFF2962FF : 0xFF6B7280);
        }
        if (imgNavSplit != null) {
            imgNavSplit.setImageResource(tab == BottomNav.SPLIT ? R.drawable.icons8_add_new_blue : R.drawable.icons8_plus_50);
        }
        if (imgNavHistory != null) {
            imgNavHistory.setImageResource(tab == BottomNav.HISTORY ? R.drawable.icons8_history_50_blue : R.drawable.icons8_history_50);
        }
        if (txtNavHistory != null) {
            txtNavHistory.setTextColor(tab == BottomNav.HISTORY ? 0xFF2962FF : 0xFF6B7280);
        }

        showDashboard();
    }

    private void updatePreview() {
        if (txtSplitAmount == null) return;
        BigDecimal total = parseTotal();
        int people = parsePeople();
        String formatted;
        if (total == null || people <= 0 || total.signum() <= 0) {
            formatted = Peso.format(0);
        } else {
            formatted = Peso.format(computeSplit(total, people).doubleValue());
        }

        if (!formatted.equals(lastSplitText)) {
            Peso.setText(txtSplitAmount, formatted);
            UIUtils.pulse(txtSplitAmount);
            lastSplitText = formatted;
        }
    }

    private void saveBill() {
        if (edtBiller == null || edtSplitPeople == null || edtTotalAmount == null) return;

        if (tilBiller != null) tilBiller.setError(null);
        if (tilSplitPeople != null) tilSplitPeople.setError(null);
        if (tilTotalAmount != null) tilTotalAmount.setError(null);

        String biller = edtBiller.getText().toString().trim();
        BigDecimal total = parseTotal();
        int people = parsePeople();

        if (biller.isEmpty()) {
            if (tilBiller != null) {
                tilBiller.setError("Enter the biller name");
                UIUtils.shake(tilBiller);
            }
            edtBiller.requestFocus();
            return;
        }
        if (people <= 0) {
            if (tilSplitPeople != null) {
                tilSplitPeople.setError("At least 1 person");
                UIUtils.shake(tilSplitPeople);
            }
            edtSplitPeople.requestFocus();
            return;
        }
        if (total == null || total.signum() <= 0) {
            if (tilTotalAmount != null) {
                tilTotalAmount.setError("Enter a valid amount");
                UIUtils.shake(tilTotalAmount);
            }
            edtTotalAmount.requestFocus();
            return;
        }

        String username = BillStorage.getCurrentUser(this);
        if (username.isEmpty()) {
            UIUtils.showBanner(this, "Please log in first.", true);
            return;
        }

        BigDecimal splitCost = computeSplit(total, people);
        String date = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date());

        BillStorage.addBill(this, username, biller,
                total.doubleValue(), people, splitCost.doubleValue(), date);

        UIUtils.showBanner(this, "Bill saved successfully!", false);

        edtBiller.setText("");
        edtSplitPeople.setText("");
        edtTotalAmount.setText("");
        edtBiller.clearFocus();
        edtSplitPeople.clearFocus();
        edtTotalAmount.clearFocus();

        updatePreview();
        showTab(BottomNav.HOME);
    }

    private BigDecimal computeSplit(BigDecimal total, int people) {
        return total.divide(new BigDecimal(people), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal parseTotal() {
        if (edtTotalAmount == null) return null;
        String text = edtTotalAmount.getText().toString().trim().replace(",", "");
        if (text.isEmpty() || text.equals(".")) return null;
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parsePeople() {
        if (edtSplitPeople == null) return 0;
        String text = edtSplitPeople.getText().toString().trim();
        if (text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Sizes the 4 recent-bill cards so together they fill all the space between
     * the "Recent Bills" header and the bottom navigation (no blank white area).
     */
    private void fitRecentBillsToScreen() {
        if (homeScroll == null || recentRecyclerView == null || recentAdapter == null) return;
        if (containerHome == null || containerHome.getVisibility() != View.VISIBLE) return;
        homeScroll.post(() -> {
            int top = recentRecyclerView.getTop();
            if (top <= 0 || homeScroll.getHeight() <= 0) return;

            float d = getResources().getDisplayMetrics().density;
            int gap = Math.round(12 * d); // must match layout_marginBottom in item_recent_bill.xml
            int available = homeScroll.getHeight() - homeScroll.getPaddingBottom() - top
                    - recentRecyclerView.getPaddingTop() - recentRecyclerView.getPaddingBottom();

            int perItem = available / RECENT_BILLS_LIMIT;
            int height = perItem - gap;

            int min = Math.round(84 * d);
            int max = Math.round(130 * d);
            recentAdapter.setItemHeightPx(Math.max(min, Math.min(max, height)));
        });
    }

    private void showDashboard() {
        TextView name = findViewById(R.id.txtAccountName);
        TextView totalSpent = findViewById(R.id.txtTotalSpent);
        TextView billCount = findViewById(R.id.txtBillSplitted);

        TextView emptyRecent = findViewById(R.id.txtEmptyBills);
        RecyclerView recentRecycler = findViewById(R.id.recentBillsRecycler);

        TextView emptyHistory = findViewById(R.id.txtEmptyHistory);
        RecyclerView historyRecycler = findViewById(R.id.historyRecycler);
        TextView txtHistorySubtitle = findViewById(R.id.txtHistorySubtitle);

        String username = BillStorage.getCurrentUser(this);
        if (name != null) name.setText(username.isEmpty() ? "Guest" : username);

        if (totalSpent != null) {
            Peso.setText(totalSpent, Peso.format(BillStorage.getTotalSpent(this, username)));
        }

        int count = BillStorage.getBillCount(this, username);
        if (billCount != null) {
            billCount.setText(count + (count == 1 ? " bill split" : " bills split"));
        }
        if (txtHistorySubtitle != null) {
            txtHistorySubtitle.setText(count + (count == 1 ? " bill split so far" : " bills split so far"));
        }

        List<BillStorage.Bill> recentBills = BillStorage.getRecentBills(this, username, RECENT_BILLS_LIMIT);
        if (recentAdapter != null) recentAdapter.setBills(recentBills);
        boolean isRecentEmpty = recentBills.isEmpty();
        if (emptyRecent != null) emptyRecent.setVisibility(isRecentEmpty ? View.VISIBLE : View.GONE);
        if (recentRecycler != null) recentRecycler.setVisibility(isRecentEmpty ? View.GONE : View.VISIBLE);
        fitRecentBillsToScreen();

        List<BillStorage.Bill> allBills = BillStorage.getRecentBills(this, username, Integer.MAX_VALUE);
        if (historyAdapter != null) historyAdapter.setBills(allBills);
        boolean isHistoryEmpty = allBills.isEmpty();
        if (emptyHistory != null) emptyHistory.setVisibility(isHistoryEmpty ? View.VISIBLE : View.GONE);
        if (historyRecycler != null) historyRecycler.setVisibility(isHistoryEmpty ? View.GONE : View.VISIBLE);
    }
}