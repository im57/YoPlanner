package ddwu.mobile.final_project.ma02_20170976;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    public static final String TAG = "ReviewAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<SurroundingsDto.ReviewDTO> list = new ArrayList<SurroundingsDto.ReviewDTO>();

    public ReviewAdapter(Context context, int layout, ArrayList<SurroundingsDto.ReviewDTO> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(list != null)
            return list.size();
        return 0;
    }

    @Override
    public SurroundingsDto.ReviewDTO getItem(int i) {
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
            viewHolder.tvName = view.findViewById(R.id.tvNameReview);
            viewHolder.tvTime = view.findViewById(R.id.tvTimeReview);
            viewHolder.tvText = view.findViewById(R.id.tvTextReview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        SurroundingsDto.ReviewDTO dto = list.get(i);

        viewHolder.tvName.setText(dto.getRName());
        viewHolder.tvTime.setText(dto.getTime());
        viewHolder.tvText.setText(dto.getText());

        return view;
    }

    public void setList(ArrayList<SurroundingsDto.ReviewDTO> list) {
        this.list = list;
    }

    static class ViewHolder {
        public TextView tvName = null;
        public TextView tvTime = null;
        public TextView tvText = null;
    }
}
