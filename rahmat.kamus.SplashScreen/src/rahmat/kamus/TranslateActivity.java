package rahmat.kamus;

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TranslateActivity extends Activity implements
		TextToSpeech.OnInitListener {

	
	final static int ENG2IND = 1;
	final static int IND2ENG = 2;
	
	final String[] dict_name = new String[] {  "Obat ke Penyakit",
			"Penyakit ke Obat" };

	private int DICT;
	private String text;
	private SQLiteDatabase db = null;
	private Cursor kamusCursor = null;
	private DBKamusHelper db_kamus_helper;
	private TextToSpeech mTts;
	private int SpeechStatus;
	private float font_size;
	private boolean autolink;

	TextView title, content;
	Button speech, close;

	Handler handler = new Handler();
	Thread searching;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Get Intent Parameter */
		this.getIntentParameter();

		/* Open Database File */
		db_kamus_helper = new DBKamusHelper(this);
		try {
			db_kamus_helper.openDataBase();
			db = db_kamus_helper.getReadableDatabase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		/* UI Initialization */
		if (checkResult())
			this.initUI();
	}

	private void getIntentParameter() {
		// TODO Auto-generated method stub
		Uri data = this.getIntent().getData();
		if (data != null) {
			DICT = Integer.parseInt(data.getQueryParameter("dict"));
			text = data.getQueryParameter("text");
		} else {
			DICT = this.getIntent().getExtras().getInt("DICT", 1);
			text = this.getIntent().getExtras().getString("TEXT");
		}
	}

	private boolean checkResult() {
		// TODO Auto-generated method stub
		String query;
		switch (DICT) {
		default:
		
		case ENG2IND:
			query = "SELECT penyakit FROM obat where lower(obat)=lower('"
					+ text.replace("'", "''") + "')";
			break;
		case IND2ENG:
			query = "SELECT obat FROM penyakit where lower(penyakit)=lower('"
					+ text.replace("'", "''") + "')";
			
		}
		kamusCursor = db.rawQuery(query, null);
		if (!kamusCursor.moveToFirst()) {
			Toast.makeText(
					this,
					"word `" + text + "` not found in " + dict_name[DICT - 1]
							+ " dictionary !", Toast.LENGTH_SHORT).show();
			this.finish();
			return false;
		} else
			return true;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.translate_reader);
		this.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mTts = new TextToSpeech(this, this);

		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.content);
		speech = (Button) findViewById(R.id.speech);
		close = (Button) findViewById(R.id.close);

		/* Define event callback function */
		speech.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (SpeechStatus == TextToSpeech.LANG_MISSING_DATA
						|| SpeechStatus == TextToSpeech.LANG_NOT_SUPPORTED)
					installLanguage();
				else
					mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			}
		});

		close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TranslateActivity.this.finish();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		/* Get Preferences */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		autolink = prefs.getBoolean("autolink", true);
		font_size = Float.parseFloat(prefs.getString("font_size", "18"));

		/* Display Result */
		title.setText(text);
		content.setTextSize(TypedValue.COMPLEX_UNIT_SP, font_size);
		content.setText(kamusCursor.getString(0));
		content.setLinkTextColor(Color.rgb(0x44, 0x44, 0xBB));

		if (autolink) {
			Pattern text_filter = Pattern
					.compile("\\b[A-Za-z]+[-']?[a-z]*+\\b");
			String translator_intent = "kamus://translate?dict=" + DICT
					+ "&text=";
			Linkify.addLinks(content, text_filter, translator_intent);
			stripUnderlines(content);
		}
	}

	private void stripUnderlines(TextView textView) {
		Spannable s = (Spannable) textView.getText();
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span : spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}

	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.drawable.readermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		float current_size = content.getTextSize();
		switch (item.getItemId()) {
		case R.id.zoomout:
			content.setTextSize(TypedValue.COMPLEX_UNIT_PX, current_size
					* (float) (1 / 1.2));
			break;
		case R.id.zoomin:
			content.setTextSize(TypedValue.COMPLEX_UNIT_PX, current_size
					* (float) 1.2);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
		super.onDestroy();
	}

	/* Implements TextToSpeech.OnInitListener. */
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			switch (DICT) {
			default:
			//case ALLDICT:
			case ENG2IND:
				SpeechStatus = mTts.setLanguage(Locale.US);
				break;
			case IND2ENG:
			
				break;
			}
			if (SpeechStatus == TextToSpeech.LANG_MISSING_DATA
					|| SpeechStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				speech.setBackgroundResource(R.drawable.speech_disabled);
			} else {
				speech.setBackgroundResource(R.drawable.speech_button);
			}
		} else {
			speech.setBackgroundResource(R.drawable.speech_disabled);
			speech.setEnabled(false);
			speech.setClickable(false);
		}
	}

	/* Install speech language if not available */
	protected void installLanguage() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
				.setMessage(
						"This language is not available.\nDo you want to install ?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent installIntent = new Intent();
								installIntent
										.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
								startActivity(installIntent);
							}
						}).setNegativeButton("No", null).show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}