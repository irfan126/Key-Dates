package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import uk.co.taskiq.keydates.R;

import java.util.List;

import app.AppController;
import model.RowRemItem;

/**
 * Created by Administrator on 24/01/2016.
 */
public class CustomRemListViewAdapter extends ArrayAdapter<RowRemItem> {

    Context context;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomRemListViewAdapter(Context context, int resourceId,
                                    List<RowRemItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView Category;
        TextView Reminder;
        TextView remDesc;
        TextView remDate;
        NetworkImageView thumbNail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowRemItem rowRemItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_rem, null);
            holder = new ViewHolder();
            holder.Reminder = (TextView) convertView.findViewById(R.id.Remider);
            holder.remDesc = (TextView) convertView.findViewById(R.id.remDesc);
            holder.remDate = (TextView) convertView.findViewById(R.id.reminderDate);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);



            //   imageLoader = AppController.getInstance().getImageLoader();
            //  holder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

            convertView.setTag(holder);
        } else

            holder = (ViewHolder) convertView.getTag();


        //holder.imageView.setImageResource(rowItem.getImageId());
        if(rowRemItem.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowRemItem.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowRemItem.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);


        if (((System.currentTimeMillis())/1000)+604800 < rowRemItem.getRemDateInt()) {


            holder.Reminder.setText(rowRemItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#222222"));


            holder.remDesc.setText(rowRemItem.getRemNotes());
            holder.remDesc.setTextColor(Color.parseColor("#222222"));

            holder.remDate.setText(rowRemItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#222222"));}

        else if ((System.currentTimeMillis())/1000 < rowRemItem.getRemDateInt() && ((System.currentTimeMillis())/1000)+604800 > rowRemItem.getRemDateInt()){

            holder.Reminder.setText(rowRemItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#FFA500"));

            holder.remDesc.setText(rowRemItem.getRemNotes());
            holder.remDesc.setTextColor(Color.parseColor("#FFA500"));

            holder.remDate.setText(rowRemItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#FFA500"));}

        else {


            holder.Reminder.setText(rowRemItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#FF5722"));

            holder.remDesc.setText(rowRemItem.getRemNotes());
            holder.remDesc.setTextColor(Color.parseColor("#FF5722"));

            holder.remDate.setText(rowRemItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#FF5722"));}



        // holder.thumbNail.setImageUrl(rowSubItem.getThumbnailUrl(), imageLoader);

        return convertView;
    }






}
