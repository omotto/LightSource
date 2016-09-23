package cat.irec.lightsource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	private FragmentTabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
	    tabHost.setup(this, getSupportFragmentManager(), R.id.contenido);
	    tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("SPECTRO"), SpectroControl.class, null);
	    tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("RGB"), RGBControl.class, null);
	    tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("CCT"), CCTControl.class, null);

	    // Si no se ha cargado un fichero de calibración obligamos a seleccionarlo.
	    if ( (((Aplicacion)this.getApplication()).getLEDs() == null) || (((Aplicacion)this.getApplication()).getChannels() == 0) ) 
        	startActivity(new Intent(this, ListFiles.class));

	}
/*
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true; 
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
	    	case R.id.acercaDe:
	    		intent = new Intent(this, AcercaDe.class);
	        	startActivity(intent);	    		
	    		break;
	        case R.id.menu_fichero:
	        	intent = new Intent(this, ListFiles.class);
	        	startActivity(intent);
	            break;
	        case R.id.menu_list:
	        	intent = new Intent(this, ListDevices.class);
	        	startActivity(intent);	        	
	        	break;	          
	        case R.id.accion_nuevo:
	        	intent = new Intent(this, AddDevices.class);
	        	startActivity(intent);
	            break;	
	        case R.id.config:
	        	intent = new Intent(this, ListSpectros.class);
	        	startActivity(intent);
	            break;	
	    }
		//return super.onOptionsItemSelected(item);
		return true; /** true -> consumimos el item, no se propaga*/
	}

}
