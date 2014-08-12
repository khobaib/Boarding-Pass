package com.seatunity.boardingpass.utilty;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Provides flexibility to show a colored text with own onClick action-event . *
 * 
 * @author Sumon
 * 
 */
public class MyCustomSpannable extends ClickableSpan {
	String url;

	/**
	 * @param Url
	 */
	public MyCustomSpannable(String Url) {
		this.url = Url;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		// Customize your Text Look if required
		// ds.setColor(Color.YELLOW);
		// ds.setFakeBoldText(true);
		// ds.setStrikeThruText(true);
		// ds.setTypeface(Typeface.SERIF);
		// ds.setUnderlineText(true);
		// ds.setShadowLayer(10, 1, 1, Color.WHITE);
		// ds.setTextSize(15);
	}

	@Override
	public void onClick(View widget) {
	}

	/**
	 * @return saved url
	 */
	public String getUrl() {
		return url;
	}
}