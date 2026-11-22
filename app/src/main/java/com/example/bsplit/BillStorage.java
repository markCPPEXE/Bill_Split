package com.example.bsplit;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BillStorage {
    private static final String PREFS = "bill_split_local_data";
    private static final String CURRENT_USER = "current_user";

    private BillStorage() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String userKey(String username, String suffix) {
        return "user_" + username.toLowerCase().trim() + "_" + suffix;
    }

    public static boolean register(Context context, String username, String password) {
        SharedPreferences p = prefs(context);
        String key = userKey(username, "password_hash");
        if (p.contains(key)) return false;
        p.edit().putString(key, hash(password)).putString(userKey(username, "display_name"), username).apply();
        return true;
    }

    public static boolean login(Context context, String username, String password) {
        String savedHash = prefs(context).getString(userKey(username, "password_hash"), null);
        return savedHash != null && savedHash.equals(hash(password));
    }

    public static void setCurrentUser(Context context, String username) {
        prefs(context).edit().putString(CURRENT_USER, username).apply();
    }

    public static String getCurrentUser(Context context) {
        return prefs(context).getString(CURRENT_USER, "");
    }

    public static void addBill(Context context, String username, String merchant,
                               double totalBill, int splitTo, double splitCost, String date) {
        SharedPreferences p = prefs(context);
        String key = userKey(username, "bills");
        JSONArray bills;
        try {
            bills = new JSONArray(p.getString(key, "[]"));
        } catch (JSONException e) {
            bills = new JSONArray();
        }

        JSONObject bill = new JSONObject();
        try {
            bill.put("merchant", merchant);
            bill.put("totalBill", totalBill);
            bill.put("splitTo", splitTo);
            bill.put("splitCost", splitCost);
            bill.put("date", date);
            // New transactions are inserted at the beginning.
            JSONArray updated = new JSONArray();
            updated.put(bill);
            for (int i = 0; i < bills.length(); i++) updated.put(bills.getJSONObject(i));
            p.edit().putString(key, updated.toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    public static List<Bill> getRecentBills(Context context, String username, int limit) {
        List<Bill> result = new ArrayList<>();
        try {
            JSONArray bills = new JSONArray(prefs(context).getString(userKey(username, "bills"), "[]"));
            for (int i = 0; i < Math.min(limit, bills.length()); i++) {
                JSONObject item = bills.getJSONObject(i);
                result.add(new Bill(
                        item.optString("merchant", "Bill"),
                        item.optDouble("totalBill", 0),
                        item.optInt("splitTo", 1),
                        item.optDouble("splitCost", 0),
                        item.optString("date", "")
                ));
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static double getTotalSpent(Context context, String username) {
        double total = 0;
        try {
            JSONArray bills = new JSONArray(prefs(context).getString(userKey(username, "bills"), "[]"));
            for (int i = 0; i < bills.length(); i++) {
                total += bills.getJSONObject(i).optDouble("splitCost", 0);
            }
        } catch (JSONException ignored) {
        }
        return total;
    }

    public static int getBillCount(Context context, String username) {
        try {
            return new JSONArray(prefs(context).getString(userKey(username, "bills"), "[]")).length();
        } catch (JSONException e) {
            return 0;
        }
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception e) {
            return value;
        }
    }

    public static class Bill {
        public final String merchant;
        public final double totalBill;
        public final int splitTo;
        public final double splitCost;
        public final String date;

        public Bill(String merchant, double totalBill, int splitTo, double splitCost, String date) {
            this.merchant = merchant;
            this.totalBill = totalBill;
            this.splitTo = splitTo;
            this.splitCost = splitCost;
            this.date = date;
        }
    }
}
