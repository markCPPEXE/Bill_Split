package com.example.bsplit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

/**
 * Shared bottom navigation (and profile icon) for Home, Split, History and Profile screens.
 * Call BottomNav.setup(this, BottomNav.HOME / SPLIT / HISTORY) in onCreate().
 */
public final class BottomNav {
    public static final int HOME = 0;
    public static final int SPLIT = 1;
    public static final int HISTORY = 2;
    public static final int PROFILE = 3; // not a tab: no bottom icon is highlighted

    private BottomNav() {}

    public static void setup(Activity activity, int selected) {
        View home = activity.findViewById(R.id.navHome);
        View split = activity.findViewById(R.id.navSplit);
        View history = activity.findViewById(R.id.navHistory);
        View profile = activity.findViewById(R.id.imgProfile);

        ImageView homeIcon = activity.findViewById(R.id.imgNavHome);
        ImageView splitIcon = activity.findViewById(R.id.imgNavSplit);
        ImageView historyIcon = activity.findViewById(R.id.imgNavHistory);

        if (homeIcon != null) {
            homeIcon.setImageResource(selected == HOME ? R.drawable.icons8_home_50_blue : R.drawable.icons8_home_50);
        }
        if (splitIcon != null) {
            splitIcon.setImageResource(selected == SPLIT ? R.drawable.icons8_add_new_blue : R.drawable.icons8_plus_50);
        }
        if (historyIcon != null) {
            historyIcon.setImageResource(selected == HISTORY ? R.drawable.icons8_history_50_blue : R.drawable.icons8_history_50);
        }

        if (home != null) {
            UIUtils.applyTouchScale(home);
            home.setOnClickListener(v -> goHome(activity, selected));
        }
        if (split != null) {
            UIUtils.applyTouchScale(split);
            split.setOnClickListener(v -> open(activity, selected, SPLIT, SplitTransactionActivity.class));
        }
        if (history != null) {
            UIUtils.applyTouchScale(history);
            history.setOnClickListener(v -> open(activity, selected, HISTORY, HistoryActivity.class));
        }
        if (profile != null) {
            UIUtils.applyTouchScale(profile);
            profile.setOnClickListener(v -> open(activity, selected, PROFILE, ProfileActivity.class));
        }
    }

    /** Back to Home (Home stays as the root screen and refreshes itself in onResume). */
    public static void goHome(Activity activity, int selected) {
        if (selected == HOME) return;
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        activity.finish();
    }

    private static void open(Activity activity, int selected, int target, Class<?> cls) {
        if (selected == target) return;
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        if (target > selected) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        if (selected != HOME) activity.finish();
    }
}
