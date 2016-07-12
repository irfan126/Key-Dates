package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Administrator on 13/11/2015.
 */
public class RowUpload implements Parcelable {

    private static final String TAG = RowUpload.class.getSimpleName();

    private int imageId;

    private String catID;
    private String category;
    private String categoryArchive;
    private String catDesc;
    private String upload;
    private String userID;
    private String uploadSum;

    public RowUpload(String catID,String category,String categoryArchive,String catDesc,String upload,String userID,String uploadSum) {
        this.imageId = imageId;
        this.catID = catID;
        this.category = category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.upload = upload;
        this.userID = userID;
        this.uploadSum = uploadSum;

        Log.d(TAG, "RowUpload Item: " + catID + category + categoryArchive + catDesc + upload + userID + uploadSum);


    }

    public int getImageId() { return imageId;   }
    public void setImageId(int imageId) {  this.imageId = imageId;  }

    public String getCatID() {  return catID;    }
    public void setCatID(String catID) {    this.catID = catID;    }

    public String getCategory() { Log.d(TAG, "getCategory: " + category);  return category;    }
    public void setCategory(String category) {  Log.d(TAG, "setCategory: " + category);  this.category = category;     }

    public String getCategoryArchive() {  return categoryArchive;   }
    public void setCategoryArchive(String categoryArchive) {  this.categoryArchive = categoryArchive;    }

    public String getCatDesc() {  return catDesc;    }
    public void setCatDesc(String catDesc) {  this.catDesc = catDesc;    }

    public String getUpload() {  return upload;    }
    public void setUpload(String upload) {  this.upload = upload;    }

    public String getUserID() {  return userID;    }
    public void setUserID(String userID) {  this.userID = userID;    }

    public String getUploadSum() {  return uploadSum;    }
    public void setUploadSum(String uploadSum) {  this.uploadSum = uploadSum;    }

    protected RowUpload(Parcel in) {
        imageId = in.readInt();
        catID = in.readString();
        category = in.readString();
        categoryArchive = in.readString();
        catDesc = in.readString();
        upload = in.readString();
        userID = in.readString();
        uploadSum = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(catID);
        dest.writeString(category);
        dest.writeString(categoryArchive);
        dest.writeString(catDesc);
        dest.writeString(upload);
        dest.writeString(userID);
        dest.writeString(uploadSum);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RowItem> CREATOR = new Parcelable.Creator<RowItem>() {
        @Override
        public RowItem createFromParcel(Parcel in) {
            return new RowItem(in);
        }

        @Override
        public RowItem[] newArray(int size) {
            return new RowItem[size];
        }
    };

}
