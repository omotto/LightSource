package cat.irec.lightsource;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SpectroArrayAdapter extends ArrayAdapter<Device> {

	private final int resource;
	private final List<Device> values;

	private final Activity context;

	public SpectroArrayAdapter(Activity context, int resource, List<Device> values) {
		super(context, resource, values);
		this.resource = resource;
		this.context = context;
		this.values = values;
	}

	static class ViewHolder {
		ImageView icon;
		RadioButton radiobutton;
		TextView name;
		TextView ip;
		TextView port;
	}

	public Device getItem(int i) {
		return values.get(i);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(resource, null);
			final ViewHolder viewHolder = new ViewHolder();
			// --
			viewHolder.name 		= (TextView) view.findViewById(R.id.name);
			viewHolder.ip 			= (TextView) view.findViewById(R.id.ip);
			viewHolder.port 		= (TextView) view.findViewById(R.id.port);
			viewHolder.radiobutton 	= (RadioButton) view.findViewById(R.id.radiobutton);
			viewHolder.icon 		= (ImageView) view.findViewById(R.id.logo);
			// --
			viewHolder.radiobutton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
		        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					MySQLiteHelper db = new MySQLiteHelper(getContext());
					Device device = (Device) viewHolder.radiobutton.getTag();
					if (isChecked) {
	                	device.setEnable(1);
	                    for (int c = 0; c < values.size(); c++) {
	                    	if (values.get(c) != device) {
	                    		values.get(c).setEnable(0);
	                    		db.updateDevice(values.get(c));
	                    	}
	                	}
	                } else {
	                	device.setEnable(0);
	                }
					db.updateDevice(device);
	                notifyDataSetChanged();
				}
			});
			// --
			view.setTag(viewHolder);
			viewHolder.radiobutton.setTag(values.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).radiobutton.setTag(values.get(position));
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.name.setText(view.getContext().getResources().getString(R.string.nombre)+": "+values.get(position).getName());
		holder.ip.setText(view.getContext().getResources().getString(R.string.ip)+": "+values.get(position).getIp());
		holder.port.setText(view.getContext().getResources().getString(R.string.puerto)+": "+String.valueOf(values.get(position).getPort()));
		if (values.get(position).getEnable() == 1)
			holder.radiobutton.setChecked(true);
		else
			holder.radiobutton.setChecked(false);
	
		return view;
	}
}