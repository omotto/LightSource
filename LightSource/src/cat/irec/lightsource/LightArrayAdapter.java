package cat.irec.lightsource;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LightArrayAdapter extends ArrayAdapter<Light> {
    
	private final Activity 		context;
    private final List<Light> 	lista;
    private final int  			resource;

	static class ViewHolder {
		ImageView icon;
		TextView name;
	}
    
    public LightArrayAdapter(Activity context, int resource, List<Light> values) {
		super(context, resource, values);
		this.resource = resource;
		this.context = context;
		this.lista = values;
	}

    public View getView(int position, View convertView, ViewGroup parent) {
    	View view = null;
    	if (convertView == null) {
    		LayoutInflater inflator = context.getLayoutInflater();
    		view = inflator.inflate(resource, null);
    		final ViewHolder viewHolder = new ViewHolder();
    		// --
    		viewHolder.name = (TextView) view.findViewById(R.id.name);
    		viewHolder.icon = (ImageView) view.findViewById(R.id.logo);
    		// --
    		view.setTag(viewHolder);
    	} else {
    		view = convertView;
    	}
   		ViewHolder holder = (ViewHolder) view.getTag();
   		holder.name.setText(view.getContext().getResources().getString(R.string.nombre)+": "+lista.get(position).getName());
   		return view;
   	}

    public int getCount() {
          return lista.size();
    }

    public Light getItem(int arg0) {
          return lista.get(arg0);
    }

    public long getItemId(int position) {
          return position;
    }
}
