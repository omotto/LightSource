package cat.irec.lightsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListLights extends ListActivity {

	private ListActivity actividad;
	private LightArrayAdapter adapter;
	private Light espectro;
	
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.actividad = this;
        
        List<Light> lights = listLights(100);

        adapter = new LightArrayAdapter(this, R.layout.list_lights_layout, lights);
		setListAdapter(adapter);
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				espectro = adapter.getItem(pos);
            	// REMOVE DIALOG 
            	AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
            	// Add the buttons
            	builder.setTitle(getResources().getString(R.string.remove));
            	builder.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int id) {
            			adapter.remove(espectro);
    	       	        adapter.notifyDataSetChanged();
    	       	        // BORRAR DE LA WEB 
    	       	        try {
    	       	        	DeleteTask task = new DeleteTask();
    	       	        	task.execute(String.valueOf(espectro.getId()));
    	       	        	task.get(5, TimeUnit.SECONDS);
	                     } catch (TimeoutException e) {
	                  	   //Tiempo excedido al conectar
	                     } catch (CancellationException e) {
	                  	   //Error al conectar con servidor
	                     } catch (Exception e) {
	                  	   //Error con tarea asíncrona
	                     }
    	       		}
            	});
            	builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int id) {
            			// User cancelled the dialog
            		}
            	});
            	// Create the AlertDialog
            	AlertDialog dialog = builder.create();
            	dialog.show();
            	dialog.setTitle(getResources().getString(R.string.remove));
				return true;
            }       
        });
    }
    
    @Override 
    protected void onListItemClick(ListView listView, View view, int position, long id) {
    	super.onListItemClick(listView, view, position, id);
    	Light light = (Light)getListAdapter().getItem(position);
    	((Aplicacion)this.getApplication()).setChannels(light.getCoef().length);
    	((Aplicacion)this.getApplication()).setCoeff(light.getCoef());
    	((Aplicacion)this.getApplication()).setSpectrum(Algorithm.CoefftoSPD(light.getCoef(), ((Aplicacion)this.getApplication()).getLEDs(), light.getCoef().length, 12));
    	Toast.makeText(this, getResources().getString(R.string.cargado)+" : " + Integer.toString(position) + " - " + light.toString(),Toast.LENGTH_LONG).show();
    	setResult(Activity.RESULT_OK);
    	finish();
    }
    
    public List<Light> listLights(int max) {
        try {
        	ListTask task = new ListTask();
            task.execute(max);
            return task.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
        	Log.e("getListado","Tiempo excedido al conectar");
        } catch (CancellationException e) {
        	Log.e("getListado","Error al conectar con servidor");        	
        } catch (Exception e) {
        	Log.e("getListado","Error con tarea asíncrona"); 
        }
        return new ArrayList<Light>();
    }
    
    private class ListTask extends AsyncTask<Integer, Void, List<Light>> {
    	@Override
    	protected List<Light> doInBackground(Integer... cantidad){
    		List<Light> result = new ArrayList<Light>();
            try {
            	URL url=new URL("http://www.ledmotive.hostinazo.com/Espectros/lista.php"+ "?max="+cantidad[0]);
            	HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            	if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
            		BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            		String linea = reader.readLine();
            		while (!linea.equals("")) {
            			// Declare varables
                    	int id, c = 0;
                    	int [] coef;
                    	String name = "";
                    	String[] tokens = linea.split("[ ,]+");
                    	List<Integer> coeff = new ArrayList<Integer>();
            			// Parse ID
            			id = Integer.parseInt(tokens[c++]);
            			// Parse Coefficients
            			coeff.add(Integer.parseInt(tokens[c++].substring(1)));
            			while(!tokens[c].endsWith("]")) coeff.add(Integer.parseInt(tokens[c++]));
            			coeff.add(Integer.parseInt(tokens[c].substring(0,tokens[c].length()-1)));
            			c++;
            			coef = new int[coeff.size()];
            			for (int i = 0; i < coeff.size(); i++) coef[i] = coeff.get(i);
            			// Parse Name
            			while (c < tokens.length) name += tokens[c++]+" "; 
            			result.add(new Light(id, name, coef));
            			linea = reader.readLine();
                    }
                    reader.close();
                    conexion.disconnect();
                    return result;
            	} else {
            		Log.e("getListado", conexion.getResponseMessage());
            		conexion.disconnect();
                    return result;
            	}
            } catch (Exception e) {
            	Log.e("getListado", e.getMessage(), e);
                return result;
            }    		
    	}
    } 
    
    private class DeleteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
        	try {
        		URL url = new URL("http://ledmotive.hostinazo.com/Espectros/borra.php" + "?id=" + param[0]);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                	BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                	String linea = reader.readLine();
                    if (!linea.equals("OK")) Log.e("DeleteTask","Error en servicio Web nueva");
                } else {
                    Log.e("DeleteTask", conexion.getResponseMessage());
                }
                conexion.disconnect();
        	} catch (Exception e) {
        		Log.e("DeleteTask", e.getMessage(), e);
        	}
        	return null;
        }
    }
}
