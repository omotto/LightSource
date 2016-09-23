package cat.irec.lightsource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;


public class GetSpectro extends FragmentActivity {

	private final static double A = +3.168205748E+02,
								B = +2.388283868E+00,
								C = -8.762833653E-04,
								D = -5.248022342E-06,
								E = +3.540662957E-09,
								F = +8.476675903E-12; 
	
	private double [][] leds = null;
	private int channels = 0;
	
	private Button bLoad;
	private ClientThread client_thread  = null;
	private Device espectrometro = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_spectro);

		leds = ((Aplicacion)getApplication()).getLEDs();
		channels = ((Aplicacion)getApplication()).getChannels();
        
        List<Device> devices = new LinkedList<Device>();
		MySQLiteHelper db = new MySQLiteHelper(this);
		devices = db.getAllSpectros();
		for (Device device : devices) if (device.getEnable() == 1) espectrometro = device;
		if (espectrometro != null) {
			if (client_thread != null) client_thread.requestStop();
			client_thread = new ClientThread(espectrometro);
			client_thread.start();
		}        
        
    	bLoad = (Button) findViewById(R.id.loadbutton);
    	bLoad.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
    			int coef[] = null;
				try {
					GetDataTask task = new GetDataTask();
		            task.execute();
		            coef= task.get(5, TimeUnit.SECONDS);
				} catch (InterruptedException e) { e.printStackTrace(); 
				} catch (ExecutionException e) { e.printStackTrace(); 
				} catch (TimeoutException e) { e.printStackTrace(); }
				// Store the generated Spectrum to use in other classes.
				if ((espectrometro != null) && (coef != null)) {
					((Aplicacion)getApplication()).setSpectrum(Algorithm.CoefftoSPD(coef, leds, channels, 12));
					((Aplicacion)getApplication()).setCoeff(coef);
    			}
    			setResult(Activity.RESULT_OK);
    	    	finish();
			}
		});
    	
    }

	// ----------------------------------------------------------------	
	// Thread que se encarga de pintar la grafica con el espectro leído
	// ----------------------------------------------------------------
	class ClientThread extends Thread {
		private Device spectro;
		private volatile boolean exit = false;
		
		private int [] coef = null;
			
		
		ClientThread (Device device) {
			this.spectro = device;					
		}

		public void requestStop() {
			this.exit = true;
			Log.d("TCP Client", "STOP SET");
		}		
		
		public int [] getCoeff(){
			return this.coef;
		}
	
		@Override
		public void run() {	
			BufferedOutputStream out;
			BufferedInputStream in;				
			byte[] out_buffer = new byte[100];
			byte[] in_buffer = new byte[512];
			// --
	        int c;
	        double offset;
			// --
			double [] y_axis = new double[401];
	        double [] x_axis = new double [257]; 
			// X Axis
	        for (int i = 1; i < 257; i++) x_axis[i-1] = A + B*i + C*i*i + D*i*i*i + E*i*i*i*i + F*i*i*i*i*i;
	        // --
	        double [] Lmayor = new double [401];
	        double [] Lmenor = new double [401];
	        int [] imenor = new int [401];
	        int [] imayor = new int [401];
	        double dmenor, dmayor;
	        for (int i = 0; i < 401; i++) {
	    		dmenor 	  = dmayor    = Double.MAX_VALUE;
	    		Lmenor[i] = Lmayor[i] = 0;	
	    		imenor[i] = imayor[i] = Integer.MAX_VALUE;
	    		for (int j = 0; j < 256; j++) {
	    			// Buscamos la Landa justo antes de la que queremos calcular
	    			if ((x_axis[j] <= (i + 380)) && (Math.abs(x_axis[j] - (i + 380)) < dmenor)) {
	    				dmenor    = Math.abs(x_axis[j] - (i + 380));
	    				Lmenor[i] = x_axis[j];
	    				imenor[i] = j;
	    			}
	    			// Buscamos la Landa justo despues de la que queremos calcular
	    			if ((x_axis[j] >= (i + 380)) && (Math.abs(x_axis[j] - (i + 380)) < dmayor)) {
	    				dmayor    = Math.abs(x_axis[j] - (i + 380));
	    				Lmayor[i] = x_axis[j];
	    				imayor[i] = j;
	    			}
	    		}
	    		if (imayor[i] == Integer.MAX_VALUE) imayor[i] = imenor[i];
	    	}
			try {
		    	InetAddress serverAddr = InetAddress.getByName(spectro.getIp());
		        Log.d("TCP Client", "Connecting...");
			    Socket socket = new Socket(serverAddr, spectro.getPort());
			    socket.setSoTimeout(99999); // Read Time-Out in miliseconds 
			    try {
			    	XYPlot mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
			        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), /*Color.rgb(0, 100, 0)*/null, null, null);
			        mySimpleXYPlot.setTicksPerRangeLabel(3);
			        // receive the message which the server sends back
			        in = new BufferedInputStream(socket.getInputStream());
			        //in.read(in_buffer, 0, 512); // Read "HELLO\r"
			        // send the message to the server			        	
			        out = new BufferedOutputStream(socket.getOutputStream());	
			        while (exit == false) {
			        	out_buffer[0] = (byte)(0xAA);
			        	out.write(out_buffer, 0, 1);
			            out.flush();
			            //Log.d("TCP Client", "Sent.");
			            // Get Data
			            in.read(in_buffer,0, 512); 		
			            //Log.d("TCP Client", "Get.");
			            // -- Get data
				        int [] y = new int [256];
				        for (int i = 0; i < 256; i++) {
				        	y[i] = (in_buffer[i*2] & 0x00ff);
				        	y[i] = (y[i] << 8);
				        	y[i] =  y[i] + ((in_buffer[1+(i*2)]) & 0x00ff);
				        }
				        // -- Calculamos el offset
				        //Log.d("TCP Client", "Offset");
				        c = 0;
				        offset = 0;
				        while (x_axis[c] < 380) { offset += y[c]; c++; }
				        offset /= c;
				        //Log.d("TCP Client", "Offset: "+ String.valueOf(offset)+" c: "+String.valueOf(c));
				        for (int i = 0; i < 256; i++) y[i] -= offset;
			            //Log.d("TCP Client", "Processed.");
				        // -- Calculamos la Intensidad para esa landa
				        for (int i = 0; i < 401; i++)
				        	y_axis[i] = (((i+380) - Lmenor[i]) * (y[imayor[i]] - y[imenor[i]]) / (Lmayor[i] - Lmenor[i])) + y[imenor[i]];	
				        // -- Normalized Spectrum
				        //Log.d("TCP Client", "Normalize.");
				    	double max_value = 0;
				        for (int i = 0; i < 401; i++) if (y_axis[i] > max_value) max_value = y_axis[i];
				    	if (max_value != 0) for (int i = 0; i < 401; i++) y_axis[i] = y_axis[i] / max_value;
				    	// --
				    	//Log.d("TCP Client", "Double to Number");
				    	Number [] yy_axis = new Number[401];
				    	Number [] xx_axis = new Number[401];
				    	for (int i = 0; i < 401; i++) {
				    		yy_axis[i] = y_axis[i];
				    		xx_axis[i] = i+380;
				    	}
				    	//Log.d("TCP Client", "Plot.");
				    	mySimpleXYPlot.clear();
				    	XYSeries serie = new SimpleXYSeries(Arrays.asList(xx_axis), Arrays.asList(yy_axis), "Spectro");
						mySimpleXYPlot.addSeries(serie, seriesFormat);
						mySimpleXYPlot.redraw();
		            }
			    	double yy [] = new double[81];
			    	for (int i = 0; i < 81; i++) yy[i] = y_axis[i*5];	
			    	//Log.d("TCP Client", "set coef");
			    	coef = Algorithm.xytoCoeff(Algorithm.XYZtoxy(Algorithm.SPDtoXYZ(yy)),leds,channels,12);
		            in.close();
		            out.close();	
		        } catch (Exception e) {
		            Log.d("TCP", "Server Error", e);
		        } finally {
		            socket.close();
		        }
		    } catch (Exception e) {
		    	Log.d("TCP", "Client Error", e);
		    }
			Log.d("THREAD","EXIT!!!");
		}
	}
	
	private class GetDataTask extends AsyncTask<String, Void, int []> {
        @Override
        protected int [] doInBackground(String... param) {
        	int [] coef = null;
        	try {
        		Log.d("TCP Client", "STOP CALL");
				client_thread.requestStop();
				while (coef == null) coef = client_thread.getCoeff(); 
        	} catch (Exception e) {
        		Log.e("SaveTask", e.getMessage(), e);
        	}
        	return coef;
        }
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (client_thread != null) client_thread.requestStop();	
        Log.d("TCP Client", "onDestroy");
    }
	
}	    