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

import model.RowUpload;

/**
 * Created by Administrator on 13/11/2015.
 */
public class CustomUploadListViewAdapter extends ArrayAdapter<RowUpload> {

    private static final String TAG = CustomUploadListViewAdapter.class.getSimpleName();

    Context context;


    public CustomUploadListViewAdapter(Context context, int resourceId,List<RowUpload> items) {
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
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowUpload rowUpload = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_upload, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.catDesc = (TextView) convertView.findViewById(R.id.catDesc);
            holder.upload = (TextView) convertView.findViewById(R.id.upload);



            convertView.setTag(holder);



        } else

        holder = (ViewHolder) convertView.getTag();

        holder.category.setText(rowUpload.getCategory());

        Log.d(TAG, "Upload List View: " + rowUpload.getCategory());
        holder.catDesc.setText(rowUpload.getCatDesc());

        if(rowUpload.getUpload().equals("0")) {holder.upload.setText("Upload");}
        else holder.upload.setText("Undo Upload");


        if(rowUpload.getCategory().contains("Home")) {holder.imageView.setImageResource(R.mipmap.ic_home_black_24dp);}
        else if(rowUpload.getCategory().contains("Car")) {holder.imageView.setImageResource(R.mipmap.ic_car_black_24dp);}
        else if(rowUpload.getCategory().contains("Finance")) {holder.imageView.setImageResource(R.mipmap.ic_account_balance_black_24dp);}
        else holder.imageView.setImageResource(R.mipmap.ic_default_black_24dp);


        return convertView;
    }


}
