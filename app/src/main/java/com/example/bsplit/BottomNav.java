package com.example.bsplit;

import android.app.Activity;
import android.content.Intent;
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
        ImageView home = activity.findViewById(R.id.navHome);
        ImageView split = activity.findViewById(R.id.navSplit);
        ImageView history = activity.findViewById(R.id.navHistory);

        home.setImageResource(selected == HOME ? R.drawable.icons8_home_50_blue : R.drawable.icons8_home_50);
        split.setImageResource(selected == SPLIT ? R.drawable.icons8_add_new_blue : R.drawable.icons8_plus_50);
        history.setImageResource(selected == HISTORY ? R.drawable.icons8_history_50_blue : R.drawable.icons8_history_50);

        home.setOnClickListener(v -> goHome(activity, selected));
        split.setOnClickListener(v -> open(activity, selected, SPLIT, SplitTransactionActivity.class));
        history.setOnClickListener(v -> open(activity, selected, HISTORY, HistoryActivity.class));

        // Profile icon in the header (top right)
        ImageView profile = activity.findViewById(R.id.imgProfile);
        if (profile != null) {
            profile.setOnClickListener(v -> open(activity, selected, PROFILE, ProfileActivity.class));
        }
    }

    /** Back to Home (Home stays as the root screen and refreshes itself in onResume). */
    public static void goHome(Activity activity, int selected) {
        if (selected == HOME) return;
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    private static void open(Activity activity, int selected, int target, Class<?> cls) {
        if (selected == target) return;
        activity.startActivity(new Intent(activity, cls));
        // Split <-> History: replace the current screen so Back always returns to Home.
        if (selected != HOME) activity.finish();
    }
}