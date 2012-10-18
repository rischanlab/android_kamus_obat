package rahmat.kamus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBKamusHelper extends SQLiteOpenHelper {
	
    //The Android's default system path of your application database.
    private static final String DB_PATH = "/data/data/rahmat.kamus/databases/";
    private static final String DB_NAME = "obatapp";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DBKamusHelper(Context context) {
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {
    	if (!isDatabaseExist()) {
    		/*By calling this method and empty database will be created into the default system path
              of your application so we are gonna be able to overwrite that database with our database.
    		*/
    		this.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database file.");
        	}
    	}
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean isDatabaseExist() {
    	File dbFile = new File(DB_PATH+DB_NAME);
    	return dbFile.exists();
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
    	
    	/* Concat Database file */
    	AssetManager am = myContext.getAssets();
    	File DBFile = new File(DB_PATH + DB_NAME);
        OutputStream os = new FileOutputStream(DBFile);
        DBFile.createNewFile();
        byte[] b = new byte[1024];
        int i, r;
        String []Files = am.list("");
        Arrays.sort(Files);
        
        i = 1;
        String fn = String.format("obatapp.db.%03d", i);
        while(Arrays.binarySearch(Files, fn)>=0) {
        	InputStream is = am.open(fn);
            while((r = is.read(b)) != -1) os.write(b, 0, r);
            is.close();
            i++;
            fn = String.format("obatapp.db.%03d", i);
        }
        os.flush();
        os.close();
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	this.deleteDatabase("kamus,db");
    }
 
    private void deleteDatabase(String string) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public synchronized void close() {
    	if(myDataBase != null) myDataBase.close();
    	super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}