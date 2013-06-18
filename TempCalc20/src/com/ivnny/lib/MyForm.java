package com.ivnny.lib;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MyForm {

	public static LinearLayout singleEntryWithButton(Context context, String hint, String buttonText){
		
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);
		
		//simple field
		EditText et = new EditText(context);
		lp = new LayoutParams(0,LayoutParams.WRAP_CONTENT,1.0f);//param makes the field take up the majority of the room
		et.setLayoutParams(lp);
		et.setHint(hint);
		et.setId(1);
		
		//simpel button
		Button b = new Button(context);
		b.setText(buttonText);
		b.setId(2);
		b.setTag(et);
		
		//adding to linear layout(view)
		ll.addView(et);
		ll.addView(b);
		
		
		return ll;
	}
}
