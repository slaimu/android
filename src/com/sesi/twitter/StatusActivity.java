package com.sesi.twitter;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity  implements OnClickListener ,TextWatcher{
    private EditText editText;
    private Button updateButton;
    private TextView textCount;
    private static final String TAG="StatusActivity";
    private static int COUNT=140;
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        editText=(EditText)findViewById(R.id.editText1);
        updateButton = (Button)findViewById(R.id.button1);
        updateButton.setOnClickListener(this);
   
        textCount =(TextView)findViewById(R.id.textView2);
        textCount.setText(Integer.toString(COUNT));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);
  
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
       return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    switch(item.getItemId()){
    case R.id.itemPrefs         : startActivity(new Intent(this,PrefsActivity.class));break;
    case R.id.itemServiceStop   : stopService  (new Intent(this,UpdaterService.class));break;
    case R.id.itemServiceStart  : startService (new Intent(this,UpdaterService.class));break;
    }
    return true;
    }
    	
  
   
    
	@Override
	public void onClick(View arg0) {
		String status=editText.getText().toString();
		new PostToTwitter().execute(status);
		Log.d(TAG,"onClicked");
       
	}

	class PostToTwitter extends AsyncTask<String,Integer,String>{

		protected void onPostExecute(String result){
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}
			
		@Override
		protected String doInBackground(String... params) {
			try{
				SesiApplication sesi = ( (SesiApplication)getApplication());
				Twitter.Status status = sesi.getTwitter().updateStatus(params[0]);
				return status.text;
				
			}catch(TwitterException e){
				Log.e(TAG,"Failed to connect to twitter service", e);
				return "Failed to post";
			
			}	
		}
	}


	@Override
	public void afterTextChanged(Editable arg0) {
		int count= COUNT-arg0.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		if(count < 10)
			textCount.setTextColor(Color.YELLOW);
		if(count < 0)
			textCount.setTextColor(Color.RED);
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}





