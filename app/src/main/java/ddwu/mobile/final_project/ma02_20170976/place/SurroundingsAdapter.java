package ddwu.mobile.final_project.ma02_20170976.place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170976.R;

public class SurroundingsAdapter extends BaseAdapter {
    public static final String TAG = "SurroundingsAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<SurroundingsDto> list;

    public SurroundingsAdapter(Context context, int layout, ArrayList<SurroundingsDto> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SurroundingsDto getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).get_id();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCafeName = view.findViewById(R.id.tvPlaceName);
            viewHolder.tvVicinity = view.findViewById(R.id.tvVicinity);
            viewHolder.tvOpen = view.findViewById(R.id.tvOpen);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        SurroundingsDto dto = list.get(i);

        viewHolder.tvCafeName.setText(dto.getName());
        viewHolder.tvVicinity.setText(dto.getVicinity());
        viewHolder.tvOpen.setText(dto.getOpen_now());

        return view;
    }

    public void setList(ArrayList<SurroundingsDto> list) {
        this.list = list;
    }

    static class ViewHolder {
        public TextView tvCafeName = null;
        public TextView tvVicinity = null;
        public TextView tvOpen = null;
    }

}
