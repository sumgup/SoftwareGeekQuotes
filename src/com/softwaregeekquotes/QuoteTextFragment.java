package com.softwaregeekquotes;

import java.util.Hashtable;

import com.softwaregeekquotes.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuoteTextFragment extends Fragment {
	
	private static final String IsFirstLaunch = "IsFirstLaunch";
	private static final String CurrentIndex = "CurrentIndex";
	private TextView quoteText;
	private TextView quoteAuthor;
	
	//Constants for context menu options
	public static final int MENU_MARK = 1;
	public static final int MENU_REMOVE = 2;

	// Index to retrieve from json
	private int currentIndex = 0;
	private String mCurrentQuoteText = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, 
	   ContextMenuInfo menuInfo) {
	  menu.add(Menu.NONE, MENU_MARK, Menu.NONE, "Share");
	}
	
	// This is executed when the user select an option
	@Override
	public boolean onContextItemSelected(MenuItem item) {

	switch (item.getItemId()) {
	    case MENU_MARK:
	    	String output = quoteText.getText().toString() + "\n" + quoteAuthor.getText().toString();
	       // mark_item(info.id);
	    	Intent i=new Intent(android.content.Intent.ACTION_SEND);
	    	i.setType("text/plain");
	    	i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Quote");
	    	i.putExtra(android.content.Intent.EXTRA_TEXT, output);
	    	startActivity(Intent.createChooser(i,"Share via"));
	    	
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	   }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.quote_text_fragment, container, false);
		
		//setRetainInstance(true);
		Typeface face=Typefaces.get(getActivity().getApplicationContext(),"impregnable_font.ttf");
	    quoteText= (TextView)rootView.findViewById(R.id.quoteText);
	    quoteAuthor = (TextView)rootView.findViewById(R.id.quoteAuthor);
	    
	    quoteText.setTypeface(face);
	    quoteAuthor.setTypeface(face);
	    
	    registerForContextMenu(quoteText);
	    registerForContextMenu(quoteAuthor);
	    
	    setCurrentQuoteIndex();
		new GetNextQuote().execute(currentIndex);
	    
		return rootView;
	}
	
	private void setCurrentQuoteIndex()
	{
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		 Boolean isFirstLaunch = preferences.getBoolean(IsFirstLaunch,false);
		 if(isFirstLaunch)
		 {
			 currentIndex = 0;
			 SharedPreferences.Editor editor = preferences.edit();
			 editor.putBoolean(IsFirstLaunch, false);
			 editor.commit();
		 }
		 else
		 {
			 // Retrieve prev index
			 int toBeDisplayedIndex = preferences.getInt(CurrentIndex, 0);
			 if (toBeDisplayedIndex > 0)
				 currentIndex = toBeDisplayedIndex;
		 }
	}
	
	private class GetNextQuote extends AsyncTask<Integer, Void, Long> {
		protected void onPostExecute(Long result) {
			String[] quoteParts = mCurrentQuoteText.split("@");
			if(quoteParts.length > 0)
			{
				String quote = quoteParts[0];
				String quoteAuth =  quoteParts[1];
			 quoteText.setText(quote);
			 quoteAuthor.setText(quoteAuth);
			}
		}

		protected Long doInBackground(Integer... params) {
			QuoteUtil qu = new QuoteUtil();
			mCurrentQuoteText = qu.GetNextQuoteText(getActivity(),
					params[0]);
			return null;
		}
	}
	
	
	public static class Typefaces {
		    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

		    public static Typeface get (Context c, String assetPath) {
		        synchronized (cache) {
		            if (!cache.containsKey(assetPath)) {
		                try {
		                    Typeface t = Typeface.createFromAsset(c.getAssets(),
		                            assetPath);
		                    cache.put(assetPath, t);
		                } catch (Exception e) {
		                    return null;
		                }
		            }
		            return cache.get(assetPath);
		        }
		    }
		}
	}
