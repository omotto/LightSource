package cat.irec.lightsource;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class SpectroControl extends Fragment {

	private static final int FRAME_LENGTH = 35; 
	private byte[] buffer = new byte[FRAME_LENGTH];
	
	private static final int READ_SPECTRO = 0x01023;
	
	private Button bSave, bLoad;
	private TextView tvIntensity;
	private SeekBar sbIntensity/*, sbSaturation*/;
	
	private View view, vv;
	
	private int channels = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.spectro_layout, container, false);

		channels = ((Aplicacion)getActivity().getApplication()).getChannels();
		
		buffer[0] = 0x02; // STX
		buffer[1] = 0x69; // IND
		for (int c = 2; c < 34; c++) buffer[c] = 0x00; // DATA
		buffer[34] = 0x03; // ETX
    	
    	// PLOT GRAPH
    	double [] spectro = ((Aplicacion)getActivity().getApplication()).getSpectrum();
    	XYPlot mySimpleXYPlot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), /*Color.rgb(0, 100, 0)*/null, null, null);
        mySimpleXYPlot.setTicksPerRangeLabel(3);
        Number[] y_axis = new Number[81]; 
        Number[] x_axis = new Number[81]; 
        for (int i = 0; i < 81; i++) { 
        	y_axis[i] = 380 + (float)(i*5);
        	x_axis[i] = spectro[i];
        }
        mySimpleXYPlot.clear();
	    XYSeries serie = new SimpleXYSeries(Arrays.asList(y_axis), Arrays.asList(x_axis), "Spectro");
		mySimpleXYPlot.addSeries(serie, seriesFormat);
		mySimpleXYPlot.redraw();
			
    	// LOAD BUTTON
    	bLoad = (Button) view.findViewById(R.id.loadbutton);
    	bLoad.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
    			vv = v;
    			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    			builder.setMessage(getResources().getString(R.string.fromwhere))
    			   .setCancelable(false)
    			   .setPositiveButton(getResources().getString(R.string.fromfile), new DialogInterface.OnClickListener() {
    			       public void onClick(DialogInterface dialog, int id) {
    			    	   Intent intent = new Intent(vv.getContext(), ListLights.class);
    			    	   startActivityForResult(intent, READ_SPECTRO);
    			       }
    			   })
    			   .setNegativeButton(getResources().getString(R.string.fromspectro), new DialogInterface.OnClickListener() {
    			       public void onClick(DialogInterface dialog, int id) {
    			    	   Intent intent = new Intent(vv.getContext(), GetSpectro.class);
    			    	   startActivityForResult(intent, READ_SPECTRO);
    			    	   
    			       }
    			   });
    			AlertDialog dialogo = builder.create();
    			dialogo.show();
			}
		});    	
    	
    	// SAVE BUTTON    	
    	bSave = (Button) view.findViewById(R.id.savebutton);
    	bSave.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
				// Create dialog to save file
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// Get the layout inflater
				LayoutInflater dialog_inflater = getActivity().getLayoutInflater();
				// Inflate and set the layout for the dialog and pass null as the parent view because its going in the dialog layout
				builder.setTitle(getResources().getString(R.string.spectroname));
				builder.setView(dialog_inflater.inflate(R.layout.dialog_layout,	null))
			    // Add action buttons
		           .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
		        	   @Override
		        	   public void onClick(DialogInterface dialog, int id) {
		        		   Dialog d = (Dialog) dialog;
		        		   //This is the input I can't get text from
		                   EditText filename = (EditText) d.findViewById(R.id.filename);
		                   int [] coef = ((Aplicacion)getActivity().getApplication()).getCoeff();
		                   int channels = ((Aplicacion)getActivity().getApplication()).getChannels();
		                   //
		                   String data = "[";
	                	   for (int c = 0; c < channels; c++) if (c < channels - 1) data = data + String.valueOf(coef[c])+","; else data = data + String.valueOf(coef[c]); 
	                	   data = data + "]";
	                	   //
	                       try {
	                    	   SaveTask task = new SaveTask();
	                    	   task.execute(filename.getText().toString(),data, String.valueOf(Calendar.getInstance().get(Calendar.SECOND)));
	                    	   task.get(5, TimeUnit.SECONDS);
	                       } catch (TimeoutException e) {
	                    	   //Tiempo excedido al conectar
	                       } catch (CancellationException e) {
	                    	   //Error al conectar con servidor
	                       } catch (Exception e) {
	                    	   //Error con tarea asíncrona
	                       }
		        	   }
					})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Exit
						}
					});      
				 builder.create().show();
			}
		});
    	
    	//sbSaturation = (SeekBar) view.findViewById(R.id.saturation);    	
    	sbIntensity = (SeekBar) view.findViewById(R.id.intensity);
    	sbIntensity.setOnSeekBarChangeListener( 
        		new OnSeekBarChangeListener() {
    	        	@Override
    	        	public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
    	        		tvIntensity.setText(getResources().getString(R.string.intensidad) + ": " + progresValue + "/" + sbIntensity.getMax());
    	        		int [] coef = ((Aplicacion)getActivity().getApplication()).getCoeff();
    	        		for (int c = 0; c < channels; c++) { 
    	        			coef[c] = (int)(coef[c] * (progresValue/100.0));
    	        			buffer[c*2+2] = (byte) ((coef[c]>>8)&0xFF);
    	        			buffer[c*2+3] = (byte) (coef[c]&0xFF);
    	        		}
    					Thread send_thread = new Thread(new SocketThread());
    		            send_thread.start();
    	        	}
    	        	
    	        	@Override
    	        	public void onStartTrackingTouch(SeekBar seekBar) {
    	        	}
    	
    	        	@Override
    	        	public void onStopTrackingTouch(SeekBar seekBar) {
    	        	}
                }
        	);
    	
    	tvIntensity = (TextView) view.findViewById(R.id.intensityText);
    	tvIntensity.setText(getResources().getString(R.string.intensidad) + ": " + sbIntensity.getProgress() + "/" + sbIntensity.getMax());
    	
    	return view;
    }
    
    private class SaveTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
        	try {
        		URL url = new URL("http://ledmotive.hostinazo.com/Espectros/nueva.php" + "?nombre=" + URLEncoder.encode(param[0], "UTF-8") + "&valores=" + URLEncoder.encode(param[1], "UTF-8") + "&fecha=" + param[2]);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                	BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                	String linea = reader.readLine();
                    if (!linea.equals("OK")) Log.e("SaveTask","Error en servicio Web nueva");
                } else {
                    Log.e("SaveTask", conexion.getResponseMessage());
                }
                conexion.disconnect();
        	} catch (Exception e) {
        		Log.e("SaveTask", e.getMessage(), e);
        	}
        	return null;
        }
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
    	super.onActivityResult(requestCode, resultCode, data); 
    	switch (requestCode) { 
        	case (READ_SPECTRO): 
        		if (resultCode == Activity.RESULT_OK) { 
        			// PLOT GRAPH
        	    	double [] spectro = ((Aplicacion)getActivity().getApplication()).getSpectrum();
        	    	XYPlot mySimpleXYPlot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
        	        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), /*Color.rgb(0, 100, 0)*/null, null, null);
        	        mySimpleXYPlot.setTicksPerRangeLabel(3);
        	        Number[] y_axis = new Number[81]; 
        	        Number[] x_axis = new Number[81]; 
        	        for (int i = 0; i < 81; i++) { 
        	        	y_axis[i] = 380 + (float)(i*5);
        	        	x_axis[i] = spectro[i];
        	        }
        	        mySimpleXYPlot.clear();
        		    XYSeries serie = new SimpleXYSeries(Arrays.asList(y_axis), Arrays.asList(x_axis), "Spectro");
        			mySimpleXYPlot.addSeries(serie, seriesFormat);
        			mySimpleXYPlot.redraw();	
        		} 
        	break; 
    	} 
    }
    
    // Open Send and close sockets to all selected devices 
 	public class SocketThread implements Runnable {
 		@Override
 		public void run() {
 			List<Socket> sockets = new ArrayList<Socket>();
 			List<Device> devices = new ArrayList<Device>();
 			MySQLiteHelper db = new MySQLiteHelper(getActivity());
 			devices = db.getAllLuminaries();
 			
 			for (Device device : devices) {
 				if (device.getEnable() == 1) {
 					try {
 						InetAddress serverAddr = InetAddress.getByName(device.getIp());
 					    Log.d("TCP Client", "Connecting...");
 					    Socket socket = new Socket(serverAddr, device.getPort());
 					    sockets.add(socket);
 					    Log.d("TCP Client", "Connected");
 					} catch (Exception e) {
 					    Log.d("TCP", "Error: " + e);
 					}
 				}
 			}
 			
 			BufferedOutputStream out;
 			for (Socket socket : sockets) {
 				try {
 			        out = new BufferedOutputStream(socket.getOutputStream());	
 			        out.write(buffer, 0, FRAME_LENGTH);
 			        out.flush(); 
 			        Log.d("TCP Client", "C: Sent.");
 			    } catch (Exception e) {
 			        Log.d("TCP", "S: Error", e);
 			    }
 			}
 			/*
			BufferedInputStream in;
			byte[] in_buffer = new byte[1];
			for (Socket socket : sockets) {
				try {
					in = new BufferedInputStream(socket.getInputStream());
					in.read(in_buffer, 0, 1);
			        Log.d("TCP Client", "C: Recieve ACK."+String.valueOf(in_buffer[0]));
			    } catch (Exception e) {
			        Log.d("TCP", "S: Error", e);
			    }
			}
 			*/
 			for (Socket socket : sockets) {
 				try {
 				    Log.d("TCP Client", "Closing...");
 				    socket.close();
 				    Log.d("TCP Client", "Closed");
 				} catch (Exception e) {
 				    Log.d("TCP", "Error: " + e);
 				}
 			}
 	    }
 	}
}