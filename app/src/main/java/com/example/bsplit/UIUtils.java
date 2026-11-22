package com.example.bsplit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class UIUtils {

    private UIUtils() {}

    public interface OnConfirmListener {
        void onConfirm();
    }

    /**
     * Shows a dynamic floating status/alert banner at the top of the activity window.
     * Drops down with a spring overshoot animation and auto-dismisses after 3 seconds.
     */
    public static void showBanner(Activity activity, String message, boolean isError) {
        if (activity == null || activity.isFinishing()) return;

        ViewGroup rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return;

        int bannerTagId = 0x7f090999;
        View oldBanner = rootView.findViewById(bannerTagId);
        if (oldBanner != null) rootView.removeView(oldBanner);

        View banner = LayoutInflater.from(activity).inflate(R.layout.view_dynamic_banner, rootView, false);
        banner.setId(bannerTagId);

        MaterialCardView card = banner.findViewById(R.id.bannerCard);
        ImageView icon = banner.findViewById(R.id.bannerIcon);
        TextView text = banner.findViewById(R.id.bannerText);

        if (card != null) {
            card.setCardBackgroundColor(isError ? 0xFFFEE2E2 : 0xFFDCFCE7);
            card.setStrokeColor(isError ? 0xFFFCA5A5 : 0xFF86EFAC);
        }
        if (icon != null) {
            icon.setImageResource(isError ? R.drawable.ic_close : R.drawable.ic_profile);
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(isError ? 0xFFDC2626 : 0xFF16A34A));
        }
        if (text != null) {
            text.setText(message);
            text.setTextColor(isError ? 0xFF991B1B : 0xFF166534);
        }

        rootView.addView(banner);

        banner.setTranslationY(-250f);
        banner.setAlpha(0f);
        banner.setScaleX(0.88f);
        banner.setScaleY(0.88f);

        banner.animate()
                .translationY(0f)
                .alpha(1f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(420)
                .setInterpolator(new OvershootInterpolator(1.3f))
                .start();

        banner.postDelayed(() -> {
            if (banner.getParent() != null) {
                banner.animate()
                        .translationY(-250f)
                        .alpha(0f)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(320)
                        .setInterpolator(new DecelerateInterpolator())
                        .withEndAction(() -> rootView.removeView(banner))
                        .start();
            }
        }, 3000);
    }

    /**
     * Performs a quick horizontal error shake animation on a view.
     */
    public static void shake(View view) {
        if (view == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -20f, 20f, -15f, 15f, -8f, 8f, 0f);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * Shows a modern confirmation popup dialog matching the app's design system.
     */
    public static void showConfirmDialog(
            Context context,
            int iconRes,
            int iconBgColor,
            int iconTintColor,
            String title,
            String message,
            String confirmText,
            int confirmBtnColor,
            OnConfirmListener listener
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_action, null);

        MaterialCardView iconBadge = dialogView.findViewById(R.id.iconBadge);
        ImageView imgBadgeIcon = dialogView.findViewById(R.id.imgBadgeIcon);
        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView messageView = dialogView.findViewById(R.id.dialogMessage);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        if (iconBadge != null) iconBadge.setCardBackgroundColor(iconBgColor);
        if (imgBadgeIcon != null) {
            imgBadgeIcon.setImageResource(iconRes);
            ImageViewCompat.setImageTintList(imgBadgeIcon, ColorStateList.valueOf(iconTintColor));
        }
        if (titleView != null) titleView.setText(title);
        if (messageView != null) messageView.setText(message);

        if (btnConfirm != null) {
            btnConfirm.setText(confirmText);
            btnConfirm.setBackgroundColor(confirmBtnColor);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnCancel != null) {
            applyTouchScale(btnCancel);
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnConfirm != null) {
            applyTouchScale(btnConfirm);
            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) listener.onConfirm();
            });
        }

        dialog.show();
    }

    /**
     * Adds smooth touch scale feedback to any view (button, card, icon).
     * Scales down slightly on press, springs back on release, and reliably triggers onClick listeners.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void applyTouchScale(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                    return true;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                            .setInterpolator(new OvershootInterpolator(2.0f)).start();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                            .setInterpolator(new OvershootInterpolator(2.0f)).start();
                    return true;
            }
            return false;
        });
    }

    /**
     * Fades in a view while sliding it up smoothly from translationY.
     */
    public static void animateEntrance(View view, long delay) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setTranslationY(45f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator(1.8f))
                .start();
    }

    /**
     * Sequentially animates multiple views with a staggered delay.
     */
    public static void animateStaggered(long startDelay, long stepDelay, View... views) {
        if (views == null) return;
        long currentDelay = startDelay;
        for (View v : views) {
            if (v != null) {
                animateEntrance(v, currentDelay);
                currentDelay += stepDelay;
            }
        }
    }

    /**
     * Performs a subtle pulse bounce animation on a view.
     */
    public static void pulse(View view) {
        if (view == null) return;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 1.08f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 1.08f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(280);
        set.setInterpolator(new OvershootInterpolator(2.5f));
        set.start();
    }
}
