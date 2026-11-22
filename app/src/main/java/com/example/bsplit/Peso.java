package com.example.bsplit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

/**
 * The peso sign (U+20B1) is missing from both Android's "monospace" font and the
 * Azeret Mono font, so it was drawn as an empty rectangle on some devices.
 * This helper draws only the peso sign with a small bundled font (res/font/peso_*.ttf)
 * and leaves every other character in the TextView's own font.
 *
 * Use: Peso.setText(textView, "Split cost : " + Peso.format(amount));
 */
public final class Peso {
    public static final char SIGN = '\u20B1';

    private static Typeface regular;
    private static Typeface bold;

    private Peso() {}

    /** 1234.5 -> "₱1,234.50" */
    public static String format(double amount) {
        return SIGN + String.format(Locale.US, "%,.2f", amount);
    }

    /** Sets the text and makes sure every ₱ in it is drawn with the bundled font. */
    public static void setText(TextView view, CharSequence text) {
        Context context = view.getContext();
        loadFonts(context);

        SpannableString spannable = new SpannableString(text);
        Typeface pesoFont = isBold(view.getTypeface()) ? bold : regular;

        if (pesoFont != null) {
            for (int i = 0; i < spannable.length(); i++) {
                if (spannable.charAt(i) == SIGN) {
                    spannable.setSpan(new PesoSpan(pesoFont), i, i + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        view.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    private static synchronized void loadFonts(Context context) {
        if (regular == null) regular = ResourcesCompat.getFont(context, R.font.peso_regular);
        if (bold == null) bold = ResourcesCompat.getFont(context, R.font.peso_bold);
    }

    private static boolean isBold(Typeface typeface) {
        if (typeface == null) return false;
        if (typeface.isBold()) return true;
        return Build.VERSION.SDK_INT >= 28 && typeface.getWeight() >= 600;
    }

    private static class PesoSpan extends MetricAffectingSpan {
        private final Typeface typeface;

        PesoSpan(Typeface typeface) {
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setTypeface(typeface);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            paint.setTypeface(typeface);
        }
    }
}