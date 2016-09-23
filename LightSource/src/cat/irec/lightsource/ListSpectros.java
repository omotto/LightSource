package cat.irec.lightsource;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListSpectros extends ListActivity {

	private Device deviceSelected;
	private SpectroArrayAdapter adapter;
	private MySQLiteHelper db;
	private ListActivity actividad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new MySQLiteHelper(this);

		actividad = this;
		
		List<Device> devices = new LinkedList<Device>();
		devices = db.getAllSpectros();

		adapter = new SpectroArrayAdapter(this, R.layout.list_spectros_layout, devices);

		setListAdapter(adapter);
		
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				deviceSelected = adapter.getItem(pos);
            	// REMOVE DIALOG 
            	AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
            	// Add the buttons
            	builder.setTitle(getResources().getString(R.string.remove));
            	builder.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int id) {
            			adapter.remove(deviceSelected);
    	       	        adapter.notifyDataSetChanged();
    	       	        db.deleteDevice(deviceSelected);
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data
		Toast.makeText(this, getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}

}
