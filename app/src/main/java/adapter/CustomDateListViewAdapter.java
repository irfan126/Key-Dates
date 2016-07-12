package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import uk.co.taskiq.keydates.R;

import java.util.List;

import app.AppController;
import model.RowDateItem;

/**
 * Created by Administrator on 22/09/2015.
 */
public class CustomDateListViewAdapter extends ArrayAdapter<RowDateItem> {

    private long futureTime1 = 0;
    // Log tag
    private static final String TAG = CustomDateListViewAdapter.class.getSimpleName();

    Context context;


    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomDateListViewAdapter(Context context, int resourceId,
                                    List<RowDateItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView Category;
        TextView Reminder;
        TextView remDate;
        TextView userID;
        ImageView uploadView;


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowDateItem rowDateItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_dateitem, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.Category = (TextView) convertView.findViewById(R.id.Category);
            holder.Reminder = (TextView) convertView.findViewById(R.id.Reminder);
            holder.remDate = (TextView) convertView.findViewById(R.id.reminderDate);
            holder.userID = (TextView) convertView.findViewById(R.id.userID);
            holder.uploadView = (ImageView) convertView.findViewById(R.id.upload);


          //  imageLoader = AppController.getInstance().getImageLoader();
            //holder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

            convertView.setTag(holder);
        } else

            holder = (ViewHolder) convertView.getTag();

        if(rowDateItem.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowDateItem.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowDateItem.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);

        if(rowDateItem.getUpload().equals("4")) {holder.uploadView.setImageResource(R.mipmap.ic_system_update_alt_black_24dp);}
        else holder.uploadView.setImageResource(0);

        holder.userID.setText(rowDateItem.getUserID());


        String prd_date = rowDateItem.getRemDate();
        Log.d(TAG, "Category: " + rowDateItem.getCatDesc());


        if (((System.currentTimeMillis())/1000)+604800 < rowDateItem.getRemDateInt()) {

            holder.Category.setText(rowDateItem.getCategory());
            holder.Category.setTextColor(Color.parseColor("#222222"));


            holder.Reminder.setText(rowDateItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#222222"));

            holder.remDate.setText(rowDateItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#222222"));}

        else if ((System.currentTimeMillis())/1000 < rowDateItem.getRemDateInt() && ((System.currentTimeMillis())/1000)+604800 > rowDateItem.getRemDateInt()){

            holder.Category.setText(rowDateItem.getCategory());
            holder.Category.setTextColor(Color.parseColor("#FFA500"));

            holder.Reminder.setText(rowDateItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#FFA500"));

            holder.remDate.setText(rowDateItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#FFA500"));}

        else {

            holder.Category.setText(rowDateItem.getCategory());
            holder.Category.setTextColor(Color.parseColor("#FF5722"));

        //    holder.catDesc.setText(rowDateItem.getCatDesc());
          //  holder.catDesc.setTextColor(Color.parseColor("#FF5722"));


            holder.Reminder.setText(rowDateItem.getReminder());
            holder.Reminder.setTextColor(Color.parseColor("#FF5722"));


            holder.remDate.setText(rowDateItem.getRemDate());
            holder.remDate.setTextColor(Color.parseColor("#FF5722"));}



        //holder.thumbNail.setImageUrl(rowSubItem.getThumbnailUrl(), imageLoader);

        return convertView;
    }




}
