package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Administrator on 20/11/2015.
 */
public class RowDownload implements Parcelable {

    private static final String TAG = RowDownload.class.getSimpleName();

    private int imageId;

    private String catID;
    private String category;
    private String categoryArchive;
    private String catDesc;
    private String act_Date;
    private String act_Title;
    private String upload;
    private String userID;
    private String uploadSum;
    private String cat_UploadID;
    private String downloadCount;

    public RowDownload(String catID,String category,String categoryArchive,String catDesc,String act_Date,String act_Title, String upload,String userID,String cat_UploadID,String uploadSum,String downloadCount) {


        this.catID = catID;
        this.category = category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.act_Date = act_Date;
        this.act_Title = act_Title;
        this.upload = upload;
        this.userID = userID;
        this.uploadSum = uploadSum;
        this.cat_UploadID = cat_UploadID;
        this.downloadCount = downloadCount;

        Log.d(TAG, "RowDownload Item: " + catID + category + categoryArchive + catDesc + upload + userID + uploadSum + " " + cat_UploadID);


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

    public String getAct_Date() {  return act_Date;    }
    public void setAct_Date(String act_date) {  this.act_Date = act_date;    }

    public String getAct_Title() {  return act_Title;    }
    public void setAct_Title(String act_Title) {  this.act_Title = act_Title;    }

    public String getUpload() {  return upload;    }
    public void setUpload(String upload) {  this.upload = upload;    }

    public String getUserID() {  return userID;    }
    public void setUserID(String userID) {  this.userID = userID;    }

    public String getUploadSum() {  return uploadSum;    }
    public void setUploadSum(String uploadSum) {  this.uploadSum = uploadSum;    }

    public String getCat_UploadID() {  return cat_UploadID;    }
    public void setCat_UploadID(String cat_UploadID) {  this.cat_UploadID = cat_UploadID;    }

    public String getDownloadCount() {  return downloadCount;    }
    public void setDownloadCount(String downloadCount) {  this.downloadCount = downloadCount;    }

    protected RowDownload(Parcel in) {
        imageId = in.readInt();
        catID = in.readString();
        category = in.readString();
        categoryArchive = in.readString();
        catDesc = in.readString();
        act_Date = in.readString();
        act_Title = in.readString();
        upload = in.readString();
        userID = in.readString();
        uploadSum = in.readString();
        cat_UploadID = in.readString();
        downloadCount = in.readString();
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
        dest.writeString(act_Date);
        dest.writeString(act_Title);
        dest.writeString(upload);
        dest.writeString(userID);
        dest.writeString(uploadSum);
        dest.writeString(cat_UploadID);
        dest.writeString(downloadCount);
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
