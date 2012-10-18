package rahmat.kamus;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
	private AlertDialog.Builder builder;
	private AlertDialog aboutDialog;
	final String[] tab_menu = {
			"All",
			"Obat ke Penyakit",
			"Penyakit ke Obat",
			"Jenis Obat"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        
        // All
        //intent = new Intent().setClass(this, SearchActivity.class);
        //intent.putExtra("DICT", 1);
        //spec = tabHost.newTabSpec("all").setIndicator(tab_menu[0], res.getDrawable(R.drawable.tab_style)).setContent(intent);
        //tabHost.addTab(spec);
        
        // English to Indonesian
        intent = new Intent().setClass(this, SearchActivity.class);
        intent.putExtra("DICT", 2);
        spec = tabHost.newTabSpec("en2id").setIndicator(tab_menu[1], res.getDrawable(R.drawable.tab_style)).setContent(intent);
        tabHost.addTab(spec);
        
        // Indonesian to English
        intent = new Intent().setClass(this, SearchActivity.class);
        intent.putExtra("DICT", 3);
        spec = tabHost.newTabSpec("id2en").setIndicator(tab_menu[2], res.getDrawable(R.drawable.tab_style)).setContent(intent);
        tabHost.addTab(spec);
        
        /**
        intent = new Intent().setClass(this, SearchActivity.class);
        intent.putExtra("DICT", 4);
        spec = tabHost.newTabSpec("kbbi").setIndicator(tab_menu[3], res.getDrawable(R.drawable.tab_style)).setContent(intent);
        tabHost.addTab(spec);
        */
        // Select Default Tab
        tabHost.setCurrentTab(0);
        
        // Create About Dialog
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.layout_root));
        builder = new AlertDialog.Builder(this)
        .setView(layout)
        .setTitle("About")
        .setNeutralButton("OK", null);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        aboutDialog = builder.create();
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.drawable.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.setting:
    	  /**
    	  Intent settingsActivity = new Intent(this.getBaseContext(), SettingActivity.class);
    	  startActivity(settingsActivity);
    	  break;
    	  **/
    	  break;
      case R.id.about:
    	  aboutDialog.show();
    	  break;
      case R.id.exit:
    	  MainActivity.this.finish();
    	  break;
      default:
        return super.onOptionsItemSelected(item);
      }
      return true;
    }
}

