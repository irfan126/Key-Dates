package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 03/09/2015.
 */
public class RowSubItem implements Parcelable {

    private int imageId;
    private String remInt;
    private String catID;
    private String Category;
    private String categoryArchive;
    private String catDesc;
    private String actDate;
    private String actDays;
    private String actRem;
    private String actExpiry;
    private String actTitle;
    private String Reminder;
    private String remArchived;
    private String imageA;
    private String imageB;
    private String remDate;
    private String remExpiry;
    private String remNotes;
    private String upload;
    private String userID;
    private String catUploadID;
    private String remUploadID;
    private String uploadSum;
   // private String thumbnailUrl;

    public RowSubItem(String remInt, String catID,String Category, String categoryArchive,String catDesc,String actDate, String actDays,String actRem,String actExpiry, String actTitle, String Reminder,
                      String remArchived,String imageA,String imageB,String remDate,String remExpiry,String remNotes,String upload,String userID,String catUploadID,
                      String remUploadID, String uploadSum  ) {


        this.imageId = imageId;
        this.remInt = remInt;
        this.catID = catID;
        this.Category = Category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.actDate = actDate;
        this.actDays = actDays;
        this.actRem = actRem;
        this.actExpiry = actExpiry;
        this.actTitle = actTitle;
        this.Reminder = Reminder;
        this.remArchived = remArchived;
        this.imageA = imageA;
        this.imageB = imageB;
        this.remDate = remDate;
        this.remExpiry = remExpiry;
        this.remNotes = remNotes;
        this.upload = upload;
        this.userID = userID;
        this.catUploadID = catUploadID;
        this.remUploadID = remUploadID;
        this.uploadSum = uploadSum;
      //  this.thumbnailUrl = thumbnailUrl;
    }
    public int getImageId() { return imageId;   }
    public void setImageId(int imageId) {  this.imageId = imageId;  }

    public String getRemInt() {  return remInt;    }
    public void setRemInt(String remInt) {  this.remInt = remInt;    }


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

    public String getActRem() {  return actRem; }
    public void setActRem(String actRem) { this.actRem = actRem; }

    public String getActExpiry() {  return actExpiry; }
    public void setActExpiry(String actExpiry) { this.actExpiry = actExpiry; }

    public String getActTitle() {  return actTitle; }
    public void setActTitle(String actTitle) { this.actTitle = actTitle; }

    public String getReminder() {  return Reminder; }
    public void setReminder(String Reminder) { this.Reminder = Reminder; }

    public String getRemArchived() {  return remArchived; }
    public void setRemArchived(String remArchived) { this.remArchived = remArchived; }

    public String getImageA() {  return imageA; }
    public void setImageA(String imageA) { this.imageA = imageA; }

    public String getImageB() {  return imageB; }
    public void setImageB(String imageB) { this.imageB = imageB; }

    public String getRemDate() {  return remDate; }
    public void setRemDate(String remDate) { this.remDate = remDate; }

    public String getRemExpiry() {  return remExpiry; }
    public void setRemExpiry(String remExpiry) { this.remExpiry = remExpiry; }

    public String getRemNotes() {  return remNotes; }
    public void setRemNotes(String remNotes) { this.remNotes = remNotes; }

    public String getUpload() {  return upload; }
    public void setUpload(String upload) { this.upload = upload; }

    public String getUserID() {  return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getCatUploadID() {  return catUploadID; }
    public void setCatUploadID(String catUploadID) { this.catUploadID = catUploadID; }

    public String getRemUploadID() {  return remUploadID; }
    public void setRemUploadID(String remUploadID) { this.remUploadID = remUploadID; }

    public String getUploadSum() {  return uploadSum; }
    public void setUploadSum(String uploadSum) { this.uploadSum = uploadSum; }

    //  public String getThumbnailUrl() { return thumbnailUrl;}
  //  public void setThumbnailUrl(String thumbnailUrl) {  this.thumbnailUrl = thumbnailUrl; }


    protected RowSubItem(Parcel in) {
        imageId = in.readInt();
        remInt = in.readString();
        catID = in.readString();
        Category = in.readString();
        categoryArchive = in.readString();
        catDesc = in.readString();
        actDate = in.readString();
        actDays = in.readString();
        actRem = in.readString();
        actExpiry = in.readString();
        actTitle = in.readString();
        Reminder = in.readString();
        remArchived = in.readString();
        imageA = in.readString();
        imageB = in.readString();
        remDate = in.readString();
        remExpiry = in.readString();
        remNotes = in.readString();
        upload = in.readString();
        userID = in.readString();
        catUploadID = in.readString();
        remUploadID = in.readString();
        uploadSum = in.readString();

       // thumbnailUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(remInt);
        dest.writeString(catID);
        dest.writeString(Category);
        dest.writeString(categoryArchive);
        dest.writeString(catDesc);
        dest.writeString(actDate);
        dest.writeString(actDays);
        dest.writeString(actRem);
        dest.writeString(actExpiry);
        dest.writeString(actTitle);
        dest.writeString(Reminder);
        dest.writeString(remArchived);
        dest.writeString(imageA);
        dest.writeString(imageB);
        dest.writeString(remDate);
        dest.writeString(remExpiry);
        dest.writeString(remNotes);
        dest.writeString(upload);
        dest.writeString(userID);
        dest.writeString(catUploadID);
        dest.writeString(remUploadID);
        dest.writeString(uploadSum);



       // dest.writeString(thumbnailUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RowSubItem> CREATOR = new Parcelable.Creator<RowSubItem>() {
        @Override
        public RowSubItem createFromParcel(Parcel in) {
            return new RowSubItem(in);
        }

        @Override
        public RowSubItem[] newArray(int size) {
            return new RowSubItem[size];
        }
    };

}
