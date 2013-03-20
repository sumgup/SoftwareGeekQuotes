package com.softwaregeekquotes;

import com.softwaregeekquotes.R;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class ScreenSlidePagerActivity extends FragmentActivity {

	
	private ViewPager mPager;
	private AsyncTask<Void, Integer, String> mAsyncTask;
	private PagerAdapter mPagerAdapter;
	private int mQuoteCount = 0;
	
	//Shared prefs
	public static final String IsFirstLaunch = "IsFirstLaunch";
	public static final String CurrentIndex = "CurrentIndex";

	@Override
	public void onDestroy() {
		if (mAsyncTask != null) {
			// Log.v("memory","On destroy Service line fragment- cancelling async task");
			mAsyncTask.cancel(true);
		}
		super.onDestroy();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_quote_view);
		final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar1);
		
		// Save current index as zero
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("IsFirstLaunch", true);
		editor.putInt("CurrentIndex", 0);
		editor.commit();
		mPager = (ViewPager) findViewById(R.id.pager);
	
		
		mAsyncTask = new GetQuoteCountTask(progress).execute();
	}
	
	

	private void populateControls() {
		// Instantiate a ViewPager and a PagerAdapter.
	
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageTransformer(true, new ZoomOutPageTransformer());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	SharedPreferences.Editor editor = preferences.edit();
	    	editor.putInt("CurrentIndex", position);
	    	editor.commit();
			return new QuoteTextFragment();
		}

		@Override
		public int getCount() {
			return mQuoteCount;
		}
	}

	private class GetQuoteCountTask extends AsyncTask<Void, Integer, String> {
		private ProgressBar progress;

		public GetQuoteCountTask(ProgressBar progress) {
			this.progress = progress;
		}

		protected void onProgressUpdate(final Integer... values) {
			progress.incrementProgressBy(30);
		}

		protected void onPostExecute(String input) {
			progress.setVisibility(View.GONE);
			populateControls();
		}

		@Override
		protected String doInBackground(Void... params) {
			
			// Get
			QuoteUtil qu = new QuoteUtil();
			mQuoteCount = qu.GetQuoteCount(getApplicationContext());
			progress.incrementProgressBy(70);
			return null;
		}
	}


}