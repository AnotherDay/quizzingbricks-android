package com.quizzingbricks.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.quizzingbricks.R;

import com.quizzingbricks.authentication.AuthenticationManager;
import com.quizzingbricks.communication.apiObjects.OnTaskCompleteAsync;
import com.quizzingbricks.communication.apiObjects.UserThreadedAPI;
import com.quizzingbricks.tools.AsyncTaskResult;
import com.quizzingbricks.tools.SimplePopupWindow;


public class LoginActivity extends Activity implements OnTaskCompleteAsync	{

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if(intent.hasExtra("Message"))	{
        	TextView textView = (TextView)findViewById(R.id.error_message_text);
        	textView.setText(intent.getStringExtra("Message"));
        	new SimplePopupWindow(this).createPopupWindow("Login error", intent.getStringExtra("Message"));
        }
        ActionBar ab = getActionBar();
        ab.setTitle("Login");
        ab.setDisplayHomeAsUpEnabled(true);
	    new UserThreadedAPI(this).getFriendsList(this);
    }
    
    @Override
	public void onBackPressed() {
		finish();
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
    //TODO: disable the button when this function is called
    public void sendLoginUserInfo(View view) {
    	EditText emailEdit = (EditText) findViewById(R.id.login_email_edit);
    	String email = emailEdit.getText().toString();
    	
    	EditText passwordEdit = (EditText) findViewById(R.id.login_password_edit);
    	String password = passwordEdit.getText().toString();
    	
    	if(email.equals("") || password.equals(""))	{
    		TextView textView = (TextView)findViewById(R.id.error_message_text);
        	textView.setText("Please fill in all the fields");
        	new SimplePopupWindow(this).createPopupWindow("Login error", "Please fill in all the fields");
    	}
    	else	{
    		AuthenticationManager authManager = new AuthenticationManager(LoginActivity.this);
    		authManager.login(email, password);
    	}
    }

	@Override
	public void onComplete(AsyncTaskResult<JSONObject> result) {
		if(result.getException() != null)	{
			result.getException().printStackTrace();
		}
	}
}
