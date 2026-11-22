package com.example.bsplit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentBillAdapter extends RecyclerView.Adapter<RecentBillAdapter.BillViewHolder> {

    private final List<BillStorage.Bill> bills = new ArrayList<>();

    public void setBills(List<BillStorage.Bill> newBills) {
        bills.clear();
        bills.addAll(newBills);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillStorage.Bill bill = bills.get(position);

        holder.merchant.setText(bill.merchant);
        holder.splitTo.setText("Split to  : " + bill.splitTo);
        holder.totalBill.setText("Total Bill : " + formatTotal(bill.totalBill));
        Peso.setText(holder.splitCost, "Split cost : " + Peso.format(bill.splitCost));
        holder.date.setText("Date       : " + bill.date);

        bindLogo(holder, bill.merchant);
    }

    /**
     * Looks for a drawable named "<merchant>_logo" (e.g. jollibee_logo, starbucks_logo).
     * If the file is not in res/drawable, a badge with the merchant's first letter is shown,
     * so the app never crashes or fails to build because of a missing logo.
     */
    private void bindLogo(BillViewHolder holder, String merchant) {
        Context context = holder.itemView.getContext();
        String key = merchant == null ? "" : merchant.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        int logoRes = key.isEmpty() ? 0 : context.getResources()
                .getIdentifier(key + "_logo", "drawable", context.getPackageName());

        if (logoRes != 0) {
            holder.logo.setImageResource(logoRes);
            holder.logo.setVisibility(View.VISIBLE);
            holder.logoLetter.setVisibility(View.GONE);
        } else {
            String letter = (merchant == null || merchant.trim().isEmpty())
                    ? "?" : merchant.trim().substring(0, 1).toUpperCase(Locale.ROOT);
            holder.logoLetter.setText(letter);
            holder.logoLetter.setVisibility(View.VISIBLE);
            holder.logo.setVisibility(View.GONE);
        }
    }

    // 599.0 -> "599", 599.5 -> "599.50"
    private String formatTotal(double value) {
        if (value == Math.rint(value)) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.2f", value);
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        final ImageView logo;
        final TextView logoLetter, merchant, splitTo, totalBill, splitCost, date;

        BillViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.imgLogo);
            logoLetter = itemView.findViewById(R.id.txtLogoLetter);
            merchant = itemView.findViewById(R.id.txtMerchant);
            splitTo = itemView.findViewById(R.id.txtSplitTo);
            totalBill = itemView.findViewById(R.id.txtTotalBill);
            splitCost = itemView.findViewById(R.id.txtSplitCost);
            date = itemView.findViewById(R.id.txtDate);
        }
    }
}