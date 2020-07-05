package ddwu.mobile.final_project.ma02_20170976;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    Cursor cursor;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(R.layout.listview_layout, parent, false);
        return listItemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvPlanContent = (TextView)view.findViewById(R.id.tvPlanContent);
        TextView tvPlanDate = (TextView)view.findViewById(R.id.tvPlanDate);
        TextView tvPlanPlace = (TextView)view.findViewById(R.id.tvPlanPlace);
        ImageView ivClock = (ImageView)view.findViewById(R.id.ivClock);
        tvPlanContent.setText(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_CONTENT)));
        tvPlanDate.setText(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_DATE)));
        tvPlanPlace.setText(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_PLACE)));
        if(!cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_TIME)).equals(""))
            ivClock.setImageResource(R.mipmap.clock);
        else
            ivClock.setImageResource(R.color.quantum_white_100);
    }
}
