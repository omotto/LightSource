package cat.irec.lightsource;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceArrayAdapter extends ArrayAdapter<Device> {

	private final int resource;
	private final List<Device> values;

	private final Activity context;

	public DeviceArrayAdapter(Activity context, int resource, List<Device> values) {
		super(context, resource, values);
		this.resource = resource;
		this.context = context;
		this.values = values;
	}

	static class ViewHolder {
		ImageView icon;
		CheckBox checkbox;
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
			viewHolder.name 	= (TextView) view.findViewById(R.id.name);
			viewHolder.ip 		= (TextView) view.findViewById(R.id.ip);
			viewHolder.port 	= (TextView) view.findViewById(R.id.port);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.icon 	= (ImageView) view.findViewById(R.id.logo);
			// --
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Device device = (Device) viewHolder.checkbox.getTag();
					if (buttonView.isChecked())
						device.setEnable(1);
					else
						device.setEnable(0);
					MySQLiteHelper db = new MySQLiteHelper(getContext());
					db.updateDevice(device);
				}
			});
			// --
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(values.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(values.get(position));
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.name.setText(view.getContext().getResources().getString(R.string.nombre)+": "+values.get(position).getName());
		holder.ip.setText(view.getContext().getResources().getString(R.string.ip)+": "+values.get(position).getIp());
		holder.port.setText(view.getContext().getResources().getString(R.string.puerto)+": "+String.valueOf(values.get(position).getPort()));
		if (values.get(position).getEnable() == 1)
			holder.checkbox.setChecked(true);
		else
			holder.checkbox.setChecked(false);
		return view;
	}
}