package adapter;

import android.app.Activity;
import android.content.Context;
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
import model.RowSubItem;

/**
 * Created by Administrator on 03/09/2015.
 */
public class CustomSubListViewAdapter extends ArrayAdapter<RowSubItem> {


    Context context;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomSubListViewAdapter(Context context, int resourceId,
                                 List<RowSubItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView Category;
        TextView Reminder;
        TextView remDesc;
        NetworkImageView thumbNail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowSubItem rowSubItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_reminder, null);
            holder = new ViewHolder();
            holder.Reminder = (TextView) convertView.findViewById(R.id.Remider);
            holder.remDesc = (TextView) convertView.findViewById(R.id.remDesc);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);



         //   imageLoader = AppController.getInstance().getImageLoader();
          //  holder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

            convertView.setTag(holder);
        } else

            holder = (ViewHolder) convertView.getTag();

        holder.Reminder.setText(rowSubItem.getReminder());
        holder.remDesc.setText(rowSubItem.getRemNotes());
        //holder.imageView.setImageResource(rowItem.getImageId());
        if(rowSubItem.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowSubItem.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowSubItem.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);




       // holder.thumbNail.setImageUrl(rowSubItem.getThumbnailUrl(), imageLoader);

        return convertView;
    }


}
