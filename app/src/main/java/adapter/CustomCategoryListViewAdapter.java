package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.taskiq.keydates.R;

import java.util.List;

import model.RowCategory;

/**
 * Created by Administrator on 28/12/2015.
 */
public class CustomCategoryListViewAdapter extends ArrayAdapter<RowCategory> {

    private static final String TAG = CustomCategoryListViewAdapter.class.getSimpleName();

    Context context;



    public CustomCategoryListViewAdapter(Context context, int resourceId,
                                 List<RowCategory> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView category;
        TextView catDesc;
        TextView userID;
        //    NetworkImageView thumbNail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowCategory rowCategory = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.catDesc = (TextView) convertView.findViewById(R.id.catDesc);
            holder.userID = (TextView) convertView.findViewById(R.id.userID);



            //      imageLoader = AppController.getInstance().getImageLoader();
            //      holder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

            convertView.setTag(holder);
        } else

            holder = (ViewHolder) convertView.getTag();

        holder.category.setText(rowCategory.getCategory());

        Log.d(TAG, "string1234: " + rowCategory.getCategory());
        holder.catDesc.setText(rowCategory.getCatDesc());
        holder.userID.setText(rowCategory.getUserID());
        //holder.imageView.setImageResource(rowItem.getImageId());
        if(rowCategory.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowCategory.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowCategory.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);

        //   holder.thumbNail.setImageUrl(rowItem.getThumbnailUrl(), imageLoader);

        return convertView;
    }

}
