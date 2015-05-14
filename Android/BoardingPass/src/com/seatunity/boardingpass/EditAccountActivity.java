package com.seatunity.boardingpass;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class EditAccountActivity extends Activity{
	private EditText etStatus,et_first_name,et_last_name;
	private TextView tv_ch_remaining;
	private int CHAR_LIMIT=70;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
		etStatus=(EditText)findViewById(R.id.et_status);
		et_first_name=(EditText)findViewById(R.id.et_first_name);
		et_last_name=(EditText)findViewById(R.id.et_last_name);
		tv_ch_remaining=(TextView)findViewById(R.id.tv_ch_remaining);
		etStatus.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 
				int size=s.toString().length();
				if((CHAR_LIMIT-size)<=0)
					tv_ch_remaining.setTextColor(getResources().getColor(R.color.red));
				else
					tv_ch_remaining.setTextColor(getResources().getColor(R.color.black_transparent_back));
				
				tv_ch_remaining.setText(""+(CHAR_LIMIT-size));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
