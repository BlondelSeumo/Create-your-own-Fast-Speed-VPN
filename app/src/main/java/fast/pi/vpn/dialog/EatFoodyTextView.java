package fast.pi.vpn.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import fast.pi.vpn.Interface.FontCache;

public class EatFoodyTextView extends TextView {

    public EatFoodyTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public EatFoodyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public EatFoodyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("averta_light.otf", context);
        setTypeface(customFont);
    }
}