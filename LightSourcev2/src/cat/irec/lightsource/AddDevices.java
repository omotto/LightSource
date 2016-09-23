package cat.irec.lightsource;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddDevices extends Activity {

	private Spinner spinner;
	private Button btnSubmit;
	private EditText ipaddress, port, name;

	View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_devices_layout);
		
		view = this.getWindow().getDecorView().findViewById(android.R.id.content);
		
		name = (EditText) findViewById(R.id.editanombre);
		port = (EditText) findViewById(R.id.editapuerto);
		ipaddress = (EditText) findViewById(R.id.editaip);
		spinner = (Spinner) findViewById(R.id.selector);
		
		List<String> list = new ArrayList<String>();
		list.add(getResources().getString(R.string.luminaria));
		list.add(getResources().getString(R.string.spectrometro));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(dataAdapter);
		btnSubmit = (Button) findViewById(R.id.boton);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (port.getText().toString().equals("")  || ipaddress.getText().toString().equals("") || name.getText().toString().equals("")) {
					Toast.makeText(view.getContext(), getResources().getString(R.string.error_fill), Toast.LENGTH_LONG).show();	
				} else {
					MySQLiteHelper db = new MySQLiteHelper(view.getContext());
					db.addDevice(new Device(name.getText().toString(), ipaddress.getText().toString(), Integer.parseInt(port.getText().toString()), spinner.getSelectedItemPosition()));
					Toast.makeText(view.getContext(), String.valueOf(spinner.getSelectedItem()) + "\n" + ipaddress.getText().toString() + "\n" + port.getText().toString() + "\n" + name.getText().toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
