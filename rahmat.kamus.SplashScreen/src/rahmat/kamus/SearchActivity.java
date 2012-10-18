package rahmat.kamus;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends ListActivity {

	final SearchActivity this_class = this;
	//final static int ALLDICT = 1;
	final static int ENG2IND = 2;
	final static int IND2ENG = 3;
	//final static int KBBIDICT = 4;
	final String arrow = String.format("%c", 0x2192);
	final String not_found = "No entries found.";

	EditText textbox;
	ListView result;
	View loading;
	ArrayList<String> list_result = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	AdapterView.OnItemClickListener translator;
	Handler handler = new Handler();
	Thread searching;
	Intent translate_dialog;

	private String search_limit;
	private int DICT;
	private SQLiteDatabase db = null;
	private Cursor kamusCursor = null;
	private DBKamusHelper db_kamus_helper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		/* UI Initialization */
		this.initUI();

		/* Open Database File */
		db_kamus_helper = new DBKamusHelper(this);
		try {
			db_kamus_helper.openDataBase();
			db = db_kamus_helper.getReadableDatabase();
		} catch (SQLException sqle) {
			throw sqle;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		getSetting();
	}

	@Override
	public void onResume() {
		super.onResume();
		getSetting();
	}

	private void getSetting() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		search_limit = prefs.getString("search_limit", "15");
	}

	protected void initUI() {
		DICT = this.getIntent().getExtras().getInt("DICT", 1);

		textbox = (EditText) findViewById(R.id.text);
		result = (ListView) findViewById(android.R.id.list);
		loading = findViewById(R.id.loadinglayer);

		/* Create List Adapter */
		adapter = new ArrayAdapter<String>(this, R.layout.list_item,
				list_result);
		setListAdapter(adapter);

		/* Defines KEY-UP Event Listener */
		searching_thread();
		textbox.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View arg0, int arg1, KeyEvent key) {
				// TODO Auto-generated method stub
				if (key.getAction() == KeyEvent.ACTION_UP
						&& key.getKeyCode() != KeyEvent.KEYCODE_MENU) {
					if (!searching.isAlive()) {
						searching_thread();
						searching.start();
					}
				}
				return false;
			}
		});

		/* Defines List-Item Click Listener */
		translator = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String text = result.getItemAtPosition(position).toString();
				if (!text.equalsIgnoreCase(not_found))
					translate(text);
			}
		};
		result.setOnItemClickListener(translator);
		result.setFocusable(false);
	}

	public void searching_thread() {
		searching = null;
		searching = new Thread(new Runnable() {
			String last = "", current;

			public void run() {
				handler.post(new Runnable() {
					public void run() {
						loading.setVisibility(View.VISIBLE);
						adapter.clear();
					}
				});
				last = textbox.getText().toString();
				search(last);
				current = textbox.getText().toString();
				if (!last.equalsIgnoreCase(current))
					run();
				else {
					handler.post(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
							loading.setVisibility(View.INVISIBLE);
						}
					});
					return;
				}
			}
		});
	}

	protected void search(String text) {
		// TODO Auto-generated method stub
		list_result.clear();
		if (text.length() > 0) {
			String query;
			switch (DICT) {
			default:
			//case ALLDICT:
				//query = "SELECT eng as word FROM english where word LIKE '"
					//	+ text.replace("'", "''")
					//	+ "%' UNION SELECT ind as word FROM indonesia where word LIKE '"
						//+ text.replace("'", "''") + "%' ORDER BY word LIMIT "
						//+ search_limit;
				//break;
			case ENG2IND:
				query = "SELECT obat FROM obat where obat LIKE '"
						+ text.replace("'", "''") + "%' ORDER BY obat LIMIT "
						+ search_limit;
				break;
			case IND2ENG:
				query = "SELECT penyakit FROM penyakit where penyakit LIKE '"
						+ text.replace("'", "''") + "%' ORDER BY penyakit LIMIT "
						+ search_limit;
				break;
				/**
			case KBBIDICT:
				query = "SELECT kata FROM kbbi where kata LIKE '"
						+ text.replace("'", "''") + "%' ORDER BY kata LIMIT "
						+ search_limit;
				break;
				*/
			}
			kamusCursor = db.rawQuery(query, null);
			if (kamusCursor.moveToFirst()) {
				list_result.add(kamusCursor.getString(0));
				while (!kamusCursor.isLast()) {
					kamusCursor.moveToNext();
					list_result.add(kamusCursor.getString(0));
				}
			} else {
				list_result.add(not_found);
			}
		}
	}

	protected void translate(String text) {
		translate_dialog = new Intent().setClass(this, TranslateActivity.class);
		translate_dialog.putExtra("DICT", DICT);
		translate_dialog.putExtra("TEXT", text);
		startActivity(translate_dialog);
	}
}
