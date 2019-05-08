package com.xiaopeng.jinglemusic2.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaopeng.jinglemusic2.R;


/**
 * @author XP-PC-XXX
 */
public class CustomTitle extends LinearLayout {

	public Button mLeft, mRight;
	public TextView mTextView;

	public CustomTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.custom_title, this);
		mLeft = (Button) findViewById(R.id.title_left);
		mRight = (Button) findViewById(R.id.title_right);
		mTextView = (TextView) findViewById(R.id.title_text);
		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((Activity) getContext()).finish();
			}
		});

		/*mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(), AboutActivity.class);
				getContext().startActivity(intent);

			}
		});*/
	}

}
