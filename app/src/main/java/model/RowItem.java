package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RowItem implements Parcelable {
    private static final String TAG = RowItem.class.getSimpleName();

    private int imageId;

    private String catID;
    private String category;
    private String categoryArchive;
    private String catDesc;
    private String actDate;

    private String userID;

  //  private String thumbnailUrl;

    public RowItem(String catID,String category,String categoryArchive,String catDesc,String actDate,String userID) {
        this.imageId = imageId;
        this.catID = catID;
        this.category = category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.actDate = actDate;

        this.userID = userID;
    //    this.thumbnailUrl = thumbnailUrl;

        Log.d(TAG, "stringCategory: " + category);
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

    public String getActDate() {  return actDate; }
    public void setActDate(String actDate) { this.actDate = actDate; }

    public String getUserID() {  return userID; }
    public void setUserID(String userID) { this.userID = userID; }


   //    public String getThumbnailUrl() { return thumbnailUrl;}
   // public void setThumbnailUrl(String thumbnailUrl) {  this.thumbnailUrl = thumbnailUrl;    }


    protected RowItem(Parcel in) {
        imageId = in.readInt();
        catID = in.readString();
        category = in.readString();
        categoryArchive = in.readString();
       catDesc = in.readString();
        actDate = in.readString();
        userID = in.readString();
     //   thumbnailUrl = in.readString();
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
        dest.writeString(actDate);
        dest.writeString(userID);
   //     dest.writeString(thumbnailUrl);
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