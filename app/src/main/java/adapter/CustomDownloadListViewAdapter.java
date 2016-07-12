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

import model.RowDownload;

/**
 * Created by Administrator on 20/11/2015.
 */
public class CustomDownloadListViewAdapter extends ArrayAdapter<RowDownload> {

    private static final String TAG = CustomDownloadListViewAdapter.class.getSimpleName();

    Context context;


    public CustomDownloadListViewAdapter(Context context, int resourceId,List<RowDownload> items) {
        super(context, resourceId, items);
        this.context = context;
    }


    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView category;
        TextView catDesc;
        TextView upload;
        TextView userID;
        TextView downloadCount;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowDownload rowDownload = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_download, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.catDesc = (TextView) convertView.findViewById(R.id.catDesc);
            holder.userID = (TextView) convertView.findViewById(R.id.userID);
            holder.downloadCount = (TextView) convertView.findViewById(R.id.downloadCount);



            convertView.setTag(holder);



        } else

            holder = (ViewHolder) convertView.getTag();

        holder.category.setText(rowDownload.getCategory());

        Log.d(TAG, "Download List View: " + rowDownload.getCategory());
        holder.catDesc.setText(rowDownload.getCatDesc());

        holder.userID.setText(rowDownload.getUserID());
        holder.downloadCount.setText(rowDownload.getDownloadCount() + " " + "Downloads");



        if(rowDownload.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowDownload.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowDownload.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);


        return convertView;
    }


}






