package com.example.bsplit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplitTransactionActivity extends AppCompatActivity {

    private EditText edtBiller, edtSplitPeople, edtTotalAmount;
    private TextView txtSplitAmount;
    private Button btnSplit;
    private String lastSplitText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.split_transaction);

        edtBiller = findViewById(R.id.edtBiller);
        edtSplitPeople = findViewById(R.id.edtSplitPeople);
        edtTotalAmount = findViewById(R.id.edtTotalAmount);
        txtSplitAmount = findViewById(R.id.txtSplitAmount);
        btnSplit = findViewById(R.id.btnSplit);

        UIUtils.applyTouchScale(btnSplit);
        UIUtils.applyTouchScale(findViewById(R.id.splitResultCard));

        UIUtils.animateStaggered(50, 60,
                findViewById(R.id.txtSplitHeading),
                findViewById(R.id.txtSplitSubheading),
                findViewById(R.id.splitInputCard),
                findViewById(R.id.splitResultCard),
                btnSplit
        );

        TextWatcher liveUpdate = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updatePreview(); }
        };
        edtSplitPeople.addTextChangedListener(liveUpdate);
        edtTotalAmount.addTextChangedListener(liveUpdate);

        edtTotalAmount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveBill();
                return true;
            }
            return false;
        });

        btnSplit.setOnClickListener(v -> saveBill());

        updatePreview();

        BottomNav.setup(this, BottomNav.SPLIT);
    }

    private void updatePreview() {
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
        String biller = edtBiller.getText().toString().trim();
        BigDecimal total = parseTotal();
        int people = parsePeople();

        if (biller.isEmpty()) {
            edtBiller.setError("Enter the biller");
            edtBiller.requestFocus();
            return;
        }
        if (people <= 0) {
            edtSplitPeople.setError("At least 1 person");
            edtSplitPeople.requestFocus();
            return;
        }
        if (total == null || total.signum() <= 0) {
            edtTotalAmount.setError("Enter a valid amount");
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

        Peso.setText(txtSplitAmount, Peso.format(splitCost.doubleValue()));
        UIUtils.pulse(txtSplitAmount);
        UIUtils.showBanner(this, "Bill saved successfully!", false);

        BottomNav.goHome(this, BottomNav.SPLIT);
    }

    private BigDecimal computeSplit(BigDecimal total, int people) {
        return total.divide(new BigDecimal(people), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal parseTotal() {
        String text = edtTotalAmount.getText().toString().trim().replace(",", "");
        if (text.isEmpty() || text.equals(".")) return null;
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parsePeople() {
        String text = edtSplitPeople.getText().toString().trim();
        if (text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
