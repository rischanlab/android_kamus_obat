package rahmat.kamus;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends Activity {

	ProgressBar progressBar;
	TextView state;
	Handler handler = new Handler();
	DBKamusHelper db_kamus_helper;
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		db_kamus_helper = new DBKamusHelper(SplashScreen.this);
		state = (TextView) findViewById(R.id.state);
		InitDatabase();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

    public void InitDatabase() {
      new Thread(new Runnable() {
         public void run() {
        	delay(1200);
 	    	if (!db_kamus_helper.isDatabaseExist()) {
 	    		handler.post(new Runnable() {
	                  public void run() {
	                	  state.setText("Installing Database ...");
	                  }
				});
				try {db_kamus_helper.createDataBase();} catch (IOException e) {e.printStackTrace();};
 	    	}
 	    	handler.post(new Runnable() {
                public void run() {
              	  state.setText("Starting ...");
                }
			});
 	    	delay(800);
 	    	handler.post(new Runnable() {
               public void run() {
                  // TODO Auto-generated method stub
                  Intent main_intent = new Intent(SplashScreen.this, MainActivity.class);
                  SplashScreen.this.startActivity(main_intent);
                  SplashScreen.this.finish();    
               } 
            });
         }
         
         public void delay(int milis) {
        	 try{
                 Thread.sleep(milis);
              }catch(InterruptedException ie){
                 ie.printStackTrace();
              }
         }
      }).start();
   }

}