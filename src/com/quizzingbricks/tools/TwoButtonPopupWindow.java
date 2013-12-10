package com.quizzingbricks.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class TwoButtonPopupWindow {

	private Context context;
	private AlertDialog.Builder dialogBuilder;
	
	public TwoButtonPopupWindow(Context context)	{
		this.context = context;
		this.dialogBuilder = new AlertDialog.Builder(context); 
	}
	
	public void createPopupWindow(String title, String message, String leftButtonText, String rightButtonText, 
			DialogInterface.OnClickListener leftButtonListener, DialogInterface.OnClickListener rightButtonListener)	{
		setTitleAndMessage(title, message);
		dialogBuilder.setNegativeButton(leftButtonText, leftButtonListener);
		dialogBuilder.setPositiveButton(rightButtonText, rightButtonListener);
		dialogBuilder.show();
	}
	
	private void setTitleAndMessage(String title, String message)	{
		dialogBuilder.setTitle(title);
    	dialogBuilder.setMessage(message);
	}
}
