package com.seatunity.boardingpass.utilty;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyCustomSpannable extends ClickableSpan
{
    String Url;
    public MyCustomSpannable(String Url) {
        this.Url = Url;
    }
    @Override
    public void updateDrawState(TextPaint ds) {
            // Customize your Text Look if required
//        ds.setColor(Color.YELLOW);
//        ds.setFakeBoldText(true);
//        ds.setStrikeThruText(true);
//        ds.setTypeface(Typeface.SERIF);
//        ds.setUnderlineText(true);
//        ds.setShadowLayer(10, 1, 1, Color.WHITE);
//        ds.setTextSize(15);
    }
    @Override
    public void onClick(View widget) {
    }
    public String getUrl() {
        return Url;
    }
}