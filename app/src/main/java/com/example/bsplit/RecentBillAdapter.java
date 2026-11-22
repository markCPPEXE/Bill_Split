package com.example.bsplit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentBillAdapter extends RecyclerView.Adapter<RecentBillAdapter.BillViewHolder> {

    public interface OnBillActionListener {
        void onDelete(int position, BillStorage.Bill bill);
    }

    private final List<BillStorage.Bill> bills = new ArrayList<>();
    private final boolean isHistoryMode;
    private OnBillActionListener listener;
    private int lastPosition = -1;

    public RecentBillAdapter() {
        this(false);
    }

    public RecentBillAdapter(boolean isHistoryMode) {
        this.isHistoryMode = isHistoryMode;
    }

    public void setOnBillActionListener(OnBillActionListener listener) {
        this.listener = listener;
    }

    public void setBills(List<BillStorage.Bill> newBills) {
        bills.clear();
        bills.addAll(newBills);
        lastPosition = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHistoryMode ? R.layout.item_history_bill : R.layout.item_recent_bill;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new BillViewHolder(view, isHistoryMode);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) pos = position;

        BillStorage.Bill bill = bills.get(pos);

        if (holder.merchant != null) holder.merchant.setText(bill.merchant);

        if (isHistoryMode) {
            if (holder.date != null) holder.date.setText(bill.date);
            if (holder.splitTo != null) {
                holder.splitTo.setText(bill.splitTo + (bill.splitTo == 1 ? " person" : " people"));
            }
            if (holder.totalBill != null) Peso.setText(holder.totalBill, Peso.format(bill.totalBill));
            if (holder.splitCost != null) Peso.setText(holder.splitCost, Peso.format(bill.splitCost));
        } else {
            if (holder.splitMeta != null) {
                holder.splitMeta.setText(bill.splitTo + (bill.splitTo == 1 ? " person \u00B7 " : " people \u00B7 ") + bill.date);
            }
            if (holder.totalBill != null) {
                holder.totalBill.setText("of " + Peso.format(bill.totalBill));
            }
            if (holder.splitCost != null) {
                Peso.setText(holder.splitCost, Peso.format(bill.splitCost));
            }
        }

        bindLogo(holder, bill.merchant);

        UIUtils.applyTouchScale(holder.itemView);
        holder.itemView.setOnClickListener(v -> showBillDetails(v.getContext(), bill));

        if (holder.btnDelete != null) {
            UIUtils.applyTouchScale(holder.btnDelete);
            final int currentPos = pos;
            holder.btnDelete.setOnClickListener(v -> confirmDelete(v.getContext(), currentPos, bill));
        }

        if (pos > lastPosition) {
            UIUtils.animateEntrance(holder.itemView, pos * 60L);
            lastPosition = pos;
        }
    }

    private void showBillDetails(Context context, BillStorage.Bill bill) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bill_details, null);

        TextView initialView = dialogView.findViewById(R.id.txtDialogBadgeInitial);
        TextView merchantView = dialogView.findViewById(R.id.txtDialogMerchant);
        TextView dateView = dialogView.findViewById(R.id.txtDialogDate);
        TextView splitCostView = dialogView.findViewById(R.id.txtDialogSplitCost);
        TextView totalBillView = dialogView.findViewById(R.id.txtDialogTotalBill);
        TextView splitPeopleView = dialogView.findViewById(R.id.txtDialogSplitPeople);
        View btnClose = dialogView.findViewById(R.id.btnDialogClose);

        if (initialView != null) {
            String letter = (bill.merchant == null || bill.merchant.trim().isEmpty())
                    ? "?" : bill.merchant.trim().substring(0, 1).toUpperCase(Locale.ROOT);
            initialView.setText(letter);
        }
        if (merchantView != null) merchantView.setText(bill.merchant);
        if (dateView != null) dateView.setText(bill.date);
        if (splitCostView != null) Peso.setText(splitCostView, Peso.format(bill.splitCost));
        if (totalBillView != null) Peso.setText(totalBillView, Peso.format(bill.totalBill));
        if (splitPeopleView != null) {
            splitPeopleView.setText(bill.splitTo + (bill.splitTo == 1 ? " person" : " people"));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnClose != null) {
            UIUtils.applyTouchScale(btnClose);
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void confirmDelete(Context context, int position, BillStorage.Bill bill) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);

        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView messageView = dialogView.findViewById(R.id.dialogMessage);
        View btnCancel = dialogView.findViewById(R.id.btnCancel);
        View btnConfirmDelete = dialogView.findViewById(R.id.btnConfirmDelete);

        if (titleView != null) {
            titleView.setText("Delete Transaction?");
        }
        if (messageView != null) {
            messageView.setText("Are you sure you want to delete the bill for \"" + bill.merchant + "\"? This action cannot be undone.");
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnCancel != null) {
            UIUtils.applyTouchScale(btnCancel);
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnConfirmDelete != null) {
            UIUtils.applyTouchScale(btnConfirmDelete);
            btnConfirmDelete.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onDelete(position, bill);
                }
            });
        }

        dialog.show();
    }

    private void bindLogo(BillViewHolder holder, String merchant) {
        if (holder.logoLetter != null) {
            String letter = (merchant == null || merchant.trim().isEmpty())
                    ? "?" : merchant.trim().substring(0, 1).toUpperCase(Locale.ROOT);
            holder.logoLetter.setText(letter);
        }
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        final TextView logoLetter, merchant, splitMeta, totalBill, splitCost, date, splitTo;
        final View btnDelete;

        BillViewHolder(@NonNull View itemView, boolean isHistoryMode) {
            super(itemView);
            logoLetter = itemView.findViewById(R.id.txtBadgeInitial);
            merchant = itemView.findViewById(R.id.txtBillTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            if (isHistoryMode) {
                splitMeta = null;
                date = itemView.findViewById(R.id.txtBillDate);
                splitCost = itemView.findViewById(R.id.txtPerPersonAmount);
                splitTo = itemView.findViewById(R.id.txtSplitPeople);
                totalBill = itemView.findViewById(R.id.txtTotalBill);
            } else {
                splitMeta = itemView.findViewById(R.id.txtBillMeta);
                date = null;
                splitCost = itemView.findViewById(R.id.txtBillAmount);
                splitTo = null;
                totalBill = itemView.findViewById(R.id.txtTotalBill);
            }
        }
    }
}
