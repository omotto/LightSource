package cat.irec.lightsource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ListFiles extends ListActivity {
	
    private File currentDir;
    private FileArrayAdapter adapter;
    private Stack<File> dirStack = new Stack<File>();

    public static final String SOURCE = "/sdcard/";
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        currentDir = new File(SOURCE); //Environment.getExternalStorageDirectory().getPath()
        fill(currentDir);
    }
        
    private void fill(File f) {
    	File[]dirs = f.listFiles();
		setTitle(getResources().getString(R.string.currentdir)+": "+f.getName());
		List<Option>dir = new ArrayList<Option>();
		List<Option>fls = new ArrayList<Option>();
		try{
			for(File ff: dirs) {
				if(ff.isDirectory()) {
					dir.add(new Option(ff.getName(),getResources().getString(R.string.carpeta),ff.getAbsolutePath()));
				} else {
					fls.add(new Option(ff.getName(),getResources().getString(R.string.filesize)+": "+ff.length(),ff.getAbsolutePath()));
				}
			}
		} catch(Exception e) {
			Log.d("FileChooser", "Error: " + e);
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) dir.add(0,new Option("..",getResources().getString(R.string.parentdir),f.getParent()));
		adapter = new FileArrayAdapter(this,R.layout.list_files_layout, dir);
		this.setListAdapter(adapter);
    }
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase(getResources().getString(R.string.carpeta))) {
			dirStack.push(currentDir);
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			if (o.getData().equalsIgnoreCase(getResources().getString(R.string.parentdir))) {
				currentDir = dirStack.pop();
				fill(currentDir);
			} else {
				onFileClick(o);
			}
		}
	}

    private void onFileClick(Option o) {
    	Log.d("FILE","Carpeta actual: "+currentDir);
    	if (o.getName().endsWith(".json")) {
    		Toast.makeText(this, getResources().getString(R.string.fichero)+" "+ o.getName() + " "+getResources().getString(R.string.cargado), Toast.LENGTH_SHORT).show();
    		// Find APP path
    		PackageManager m = getPackageManager();
    		String s = getPackageName();
    		try {
    			PackageInfo p = m.getPackageInfo(s, 0);
    			s = p.applicationInfo.dataDir;
    		} catch (NameNotFoundException e) {
    			Log.d("FILE", "Error Package name not found ", e);
    		}
    		Log.d("FILE", "File "+s+o.getName());
    		// Copy file
            File source = new File(currentDir, o.getName());
        	File destination = new File(s,"calibration.json");
        	InputStream in = null;
       		OutputStream out = null;
       		try {
       			in = new FileInputStream(source);
       			out = new FileOutputStream(destination);
       			byte[] buffer = new byte[1024];
       			int length;
       			while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
           	    in.close();
           	    out.close();
       		} catch (IOException e) {
				Log.d("FILE","ERROR: "+e);
				e.printStackTrace();
       		}
       		// Read File and load it
       		// READ JSON FILE CONFIGURATION
        	String fileContent = null;
        	File file = new File(s,"calibration.json");
        	try {
        		BufferedReader br = new BufferedReader(new FileReader(file));
        		try {
        			StringBuilder sb = new StringBuilder();
        			String line = null;
        			while ((line = br.readLine()) != null) sb.append(line).append("\n");
        			fileContent = sb.toString();
        			br.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	} catch (FileNotFoundException e) {
        		e.printStackTrace();
        	} 
        	JSONObject reader = null;
    		try { 
    			reader = new JSONObject(fileContent); 
        		JSONArray LED_spectros = reader.getJSONArray("spectra");
    	    	int channels = reader.getInt("nChannels");
    	    	double [][] leds = new double [channels][81];
    	    	for (int i = 0; i < channels; i++) {
    	    		JSONArray channel = LED_spectros.getJSONArray(i);
    	    		for (int j = 0; j < 81; j++) leds[i][j] = channel.getDouble(j);
    	    	}
        		// SET it in Application class
    			((Aplicacion)getApplication()).setLEDs(leds);
    			((Aplicacion)getApplication()).setChannels(channels);
    		} catch (JSONException e) {	
    			e.printStackTrace(); 
    		}
    	} else {
    		Toast.makeText(this, getResources().getString(R.string.fileclick)+": "+o.getName(), Toast.LENGTH_SHORT).show();
    	}
    }
}