package com.softwaregeekquotes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class QuoteUtil {

	public int GetQuoteCount(Context context) {
		DownloadJson(context);

		JsonParser jsonParser = new JsonParser();
		JsonArray results = jsonParser
				.parse(getStringFromCachedJsonFile(context)).getAsJsonObject()
				.get("Quotes").getAsJsonObject().getAsJsonArray("Quote");

		return results.size();
	}

	public String GetNextQuoteText(Context context, int displayId) {
		String nextQuote = "";
		try {
			nextQuote =  GetNextQuoteFromJson(context, displayId);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nextQuote;
	}

	private String GetNextQuoteFromJson(Context context, int displayId)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {

		String nextQuoteText = "";
		JsonParser jsonParser = new JsonParser();
		JsonArray results = jsonParser
				.parse(getStringFromCachedJsonFile(context)).getAsJsonObject()
				.get("Quotes").getAsJsonObject().getAsJsonArray("Quote");

		for (JsonElement quote : results) {

			int currentId = quote.getAsJsonObject().get("id").getAsInt();
			if (currentId == displayId) {
				String quoteText = quote.getAsJsonObject().get("text")
						.getAsString();
				String author = quote.getAsJsonObject().get("author")
						.getAsString();
				nextQuoteText = quoteText + "@" + author;
				break;
			}
		}

		return nextQuoteText;
	}

	private String getStringFromCachedJsonFile(Context context) {

		File jsonFile;
		jsonFile = new File(context.getCacheDir().getPath() + "/"
				+ "quotes.json");

		// Read from it and return the string
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(jsonFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder total = new StringBuilder();

		String line;
		try {
			while ((line = br.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total.toString();
	}

	
	private void DownloadJson(Context context) {

		// Check if JSON exists in SD card
		File existingJsonFile;
		existingJsonFile = new File(context.getCacheDir().getPath() + "/"
				+ "quotes.json");
		if (existingJsonFile.exists())
			existingJsonFile.delete();

		URL url = null;
		try {
			url = new URL("http://dl.dropbox.com/u/414360/Cpquotes.json");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection.setFollowRedirects(true);
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(false);
			con.setReadTimeout(20000);
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");
			((HttpURLConnection) con).setRequestMethod("GET");
			// System.out.println(con.getContentLength()) ;
			con.setConnectTimeout(5000);
			BufferedInputStream in = new BufferedInputStream(
					con.getInputStream());
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println(responseCode);
			}
			StringBuffer buffer = new StringBuffer();
			int chars_read;
			while ((chars_read = in.read()) != -1) {
				char g = (char) chars_read;
				buffer.append(g);
			}

			final String json = buffer.toString();
			File jsonFile;
			FileWriter writer = null;
			jsonFile = new File(context.getCacheDir() + "/" + "quotes.json");
			writer = new FileWriter(jsonFile);
			writer.write(json);
			writer.close();
		} catch (IOException e) {

		}

	}

}
