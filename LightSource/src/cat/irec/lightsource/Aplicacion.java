package cat.irec.lightsource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Aplicacion extends Application {
    private double [] Spectrum = null;
    private double [][] LEDs = null;
    private int [] Coef = null;
    private int Channels = 0;

    @Override
    public void onCreate() {
    	this.Spectrum = new double[81];
    	// READ JSON FILE CONFIGURATION
    	PackageManager m = getPackageManager();
    	String s = getPackageName();
    	try {
    		PackageInfo p = m.getPackageInfo(s, 0);
    		s = p.applicationInfo.dataDir;
    	} catch (NameNotFoundException e) {
    		Log.d("FILE", "Error Package name not found ", e);
    	}
    	// Read Data
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
    			JSONObject reader = null;
    			try { 
    				reader = new JSONObject(fileContent); 
    	    		JSONArray LED_spectros = reader.getJSONArray("spectra");
    		    	this.Channels = reader.getInt("nChannels");
    		    	this.LEDs = new double [this.Channels][81];
    		    	this.Coef = new int [this.Channels];
    		    	for (int i = 0; i < this.Channels; i++) {
    		    		this.Coef[i] = 0;
    		    		JSONArray channel = LED_spectros.getJSONArray(i);
    		    		for (int j = 0; j < 81; j++) this.LEDs[i][j] = channel.getDouble(j);
    		    	}
    			} catch (JSONException e) {	
    				e.printStackTrace(); 
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} 
    }

    public double [] getSpectrum() {
    	return this.Spectrum;
    }
   
    public void setSpectrum(double [] spectrum) {
    	System.arraycopy(spectrum, 0, this.Spectrum, 0, 81);
    }
    
    public double [][] getLEDs() {
    	return this.LEDs;
    }
    
    public void setLEDs(double [][] leds) {
    	this.LEDs = new double [leds.length][81];
    	for (int i = 0; i < leds.length; i++) 
    	    System.arraycopy(leds[i], 0, this.LEDs[i], 0, 81);
    }
    
    public int getChannels() {
    	return this.Channels;
    }
    
    public void setChannels(int channels) {
    	this.Channels = channels;
    }
    
    public int [] getCoeff() {
    	int [] coef = new int [Coef.length];
    	System.arraycopy(this.Coef, 0, coef, 0, Coef.length);
    	return coef;
    }
    
    public void setCoeff(int [] coef) {
    	this.Coef = new int [coef.length];
    	System.arraycopy(coef, 0, this.Coef, 0, coef.length);
    }

}