package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Diary> diarylist;
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context context, int layout, ArrayList<Diary> myDatalist) {
        this.context = context;
        this.layout = layout;
        this.diarylist = myDatalist;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return diarylist.size();
    }

    @Override
    public Object getItem(int position) {
        return diarylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return diarylist.get(position).get_id();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        final int position = pos;
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layout, viewGroup, false);

            holder = new ViewHolder();
            /*tvDate tvWeather tvTitle imageView*/

            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvWeather = (TextView) convertView.findViewById(R.id.tvWeather);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(diarylist.get(position).getTitle());
        holder.tvDate.setText(diarylist.get(position).getDate());
        holder.tvWeather.setText(diarylist.get(position).getWeather());

        return convertView;

    }

    static class ViewHolder{
        TextView tvTitle;
        TextView tvWeather;
        TextView tvDate;
    }
}
