package com.sesi.twitter;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {

	private static final String TAG = "UpdaterService";
	
	static final int DELAY= 60000;
	private boolean runFlag = false;
	private Updater updater; 
	SesiApplication sesi;
	
	
	//DbHelper dbHelper;
   // StatusData status;
	SQLiteDatabase db;
	
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.sesi =(SesiApplication)getApplication();
		this.updater = new Updater();
		
		//status = new StatusData(this);
		
		Log.d(TAG,"onCreatedService");
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.sesi.setServiceRunning(false);
		Log.d(TAG,"onDestroyedService");
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	//	super.onStartCommand(intent, flags, startId);
		
		if( !runFlag){
			this.runFlag =true;
			this.updater.start();
			((SesiApplication)super.getApplication()).setServiceRunning(true);
			Log.d(TAG,"onStartedService");
		}
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private class Updater extends Thread{
		public Updater(){
			super("updaterService-Updater");
		}
		
		
		@Override
		public void run(){
			UpdaterService updaterService = UpdaterService.this;
			while(updaterService.runFlag){
				Log.d(TAG,"Running background thread");
				
				try{
					SesiApplication sesi = (SesiApplication)updaterService.getApplication();
					int newUpdates = sesi.fetchStatusUpdates();
					if(newUpdates > 0){
						Log.d(TAG,"we have a new status");
					}
					Thread.sleep(DELAY);
					
				}catch(InterruptedException e){
					updaterService.runFlag = false;
				}
				
			}
		}
	}
	
	
	/*
	
	private class Updater extends Thread {
		List<Twitter.Status> timeline;
		public Updater(){
			super("UpdaterService-Updater");
		}

		@Override
		public void run(){
			UpdaterService updaterService = UpdaterService.this;

			
			while(updaterService.runFlag){
				Log.d(TAG,"Running background thread");
				
				try{
					try{
						timeline = sesi.getTwitter().getFriendsTimeline();
					}catch(TwitterException e){
					Log.e(TAG,"Failed to connect to twitter service",e);	
					}
					
					db = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					for(Twitter.Status status : timeline){
						values.clear();
						values.put(DbHelper.C_ID, status.id);
						values.put(DbHelper.C_CREATED_AT, status.createdAt.getTime());
						values.put(DbHelper.C_TEXT, status.text);
						values.put(DbHelper.C_USER, status.user.name);
					
						
						try{
						db.insertOrThrow(DbHelper.TABLE, null, values);
						Log.d(TAG,String.format("%s : %s",status.user.name,status.text));
						}catch(SQLException e){
						
						}
						
						
						
						
					}
					db.close();
					
					Log.d(TAG,"Updater ran");
					Thread.sleep(DELAY);
				}catch(InterruptedException e){
					updaterService.runFlag = false;
				}
			}
		}
	}*/
}