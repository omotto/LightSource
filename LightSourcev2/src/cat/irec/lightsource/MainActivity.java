package cat.irec.lightsource;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements TabListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    ActionBar actionBar = getSupportActionBar();         
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("SPECTRO").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("RGB").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("CCT").setTabListener(this));    	    
	    
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


	@Override
	public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) { }

	@Override
	public void onTabSelected(Tab arg0,	android.support.v4.app.FragmentTransaction arg1) { 
		Intent intent = null;
		switch (arg0.getPosition()) {
			case 0: //Spectro Control
				SpectroControl SpectroControlFragment = new SpectroControl();
		        FragmentTransaction SpectroControlTransaccion = getFragmentManager().beginTransaction();
		        SpectroControlTransaccion.replace(R.id.contenido, SpectroControlFragment);
		        SpectroControlTransaccion.addToBackStack(null);
		        SpectroControlTransaccion.commit();
	        break;
	    	case 1: //RGB Control
				RGBControl RGBControlFragment = new RGBControl();
		        FragmentTransaction RGBControlTransaccion = getFragmentManager().beginTransaction();
		        RGBControlTransaccion.replace(R.id.contenido, RGBControlFragment);
		        RGBControlTransaccion.addToBackStack(null);
		        RGBControlTransaccion.commit();
	        break;
	    	case 2: //CCT Control
				CCTControl CCTControlFragment = new CCTControl();
		        FragmentTransaction CCTControlTransaccion = getFragmentManager().beginTransaction();
		        CCTControlTransaccion.replace(R.id.contenido, CCTControlFragment);
		        CCTControlTransaccion.addToBackStack(null);
		        CCTControlTransaccion.commit();
	        break;
		}		
	}

	@Override
	public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) { }

}
