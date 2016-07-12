package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 28/12/2015.
 */
public class RowCategory implements Parcelable {

    private static final String TAG = RowCategory.class.getSimpleName();

    private String catID;
    private String Category;
    private String categoryArchive;
    private String catDesc;
    private String actDate;
    private String actDays;
    private String actTitle;
    private String upload;
    private String userID;
    private String uploadSum;
    private String catUploadID;

    public RowCategory(String catID,String Category, String categoryArchive,String catDesc,String actDate, String actDays, String actTitle,
                     String upload,String userID, String uploadSum, String catUploadID ) {



        this.catID = catID;
        this.Category = Category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.actDate = actDate;
        this.actDays = actDays;
        this.actTitle = actTitle;
        this.upload = upload;
        this.userID = userID;
        this.uploadSum = uploadSum;
        this.catUploadID = catUploadID;
        //  this.thumbnailUrl = thumbnailUrl;
    }

    public String getCatID() {  return catID;    }
    public void setCatID(String catID) {  this.catID = catID;    }

    public String getCategory() {   return Category;    }
    public void setCategory(String Category) {  this.Category = Category;    }

    public String getCategoryArchive() {   return categoryArchive;    }
    public void setCategoryArchive(String categoryArchive) {  this.categoryArchive = categoryArchive;    }

    public String getCatDesc() {  return catDesc; }
    public void setCatDesc(String catDesc) {  this.catDesc = catDesc;  }

    public String getActDate() {  return actDate; }
    public void setActDate(String actDate) { this.actDate = actDate; }

    public String getActDays() {  return actDays; }
    public void setActDays(String actDays) { this.actDays = actDays; }

    public String getActTitle() {  return actTitle; }
    public void setActTitle(String actTitle) { this.actTitle = actTitle; }

    public String getUpload() {  return upload; }
    public void setUpload(String upload) { this.upload = upload; }

    public String getUserID() {  return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getUploadSum() {  return uploadSum; }
    public void setUploadSum(String uploadSum) { this.uploadSum = uploadSum; }


    public String getCatUploadID() {  return catUploadID; }
    public void setCatUploadID(String catUploadID) { this.catUploadID = catUploadID; }



    protected RowCategory(Parcel in) {

        catID = in.readString();
        Category = in.readString();
        categoryArchive = in.readString();
        catDesc = in.readString();
        actDate = in.readString();
        actDays = in.readString();
        actTitle = in.readString();
        upload = in.readString();
        userID = in.readString();
        uploadSum = in.readString();
        catUploadID= in.readString();

        // thumbnailUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(catID);
        dest.writeString(Category);
        dest.writeString(categoryArchive);
        dest.writeString(catDesc);
        dest.writeString(actDate);
        dest.writeString(actDays);
        dest.writeString(actTitle);
        dest.writeString(upload);
        dest.writeString(userID);
        dest.writeString(uploadSum);
        dest.writeString(catUploadID);



        // dest.writeString(thumbnailUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RowCategory> CREATOR = new Parcelable.Creator<RowCategory>() {
        @Override
        public RowCategory createFromParcel(Parcel in) {
            return new RowCategory(in);
        }

        @Override
        public RowCategory[] newArray(int size) {
            return new RowCategory[size];
        }
    };

}
