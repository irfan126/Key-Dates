package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 22/09/2015.
 */
public class RowDateItem implements Parcelable {


    private int imageId;
    private String remInt;
    private String catID;
    private String Category;
    private String categoryArchive;
    private String catDesc;
    private String Reminder;
    private String reminderArchive;
    private String remDate;
    private Integer remDateInt;
    private String userID;
    private String upload;

    public RowDateItem(String remInt, String catID, String Category,String categoryArchive,String catDesc, String Reminder, String reminderArchive, String remDate,Integer remDateInt, String userID, String upload) {

        this.remInt = remInt;
        this.catID = catID;
        this.Category = Category;
        this.categoryArchive = categoryArchive;
        this.catDesc = catDesc;
        this.Reminder = Reminder;
        this.reminderArchive = reminderArchive;
        this.remDate = remDate;
        this.remDateInt = remDateInt;
        this.userID = userID;
        this.upload = upload;

    }

    public String getRemInt() {  return remInt;    }
    public void setRemInt(String remInt) {  this.remInt = remInt;    }

    public String getCatID() {   return catID;    }
    public void setCatID(String catID) {  this.catID = catID;    }

    public String getCategory() {   return Category;    }
    public void setCategory(String Category) {  this.Category = Category;    }

    public String getCategoryArchive() {  return categoryArchive; }
    public void setCategoryArchive(String categoryArchive) {  this.categoryArchive = categoryArchive;  }

    public String getCatDesc() {  return catDesc; }
    public void setCatDesc(String catDesc) { this.catDesc = catDesc; }

    public String getReminder() {  return Reminder; }
    public void setReminder(String Reminder) { this.Reminder = Reminder; }

    public String getReminderArchive() {  return reminderArchive; }
    public void setReminderArchive(String reminderArchive) { this.reminderArchive = reminderArchive; }


    public String getRemDate() {  return remDate; }
    public void setRemDate(String remDate) { this.remDate = remDate; }

    public Integer getRemDateInt() {  return remDateInt; }
    public void setRemDateInt(Integer remDateInt) { this.remDateInt = remDateInt; }

    public String getUserID() {  return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getUpload() {  return upload; }
    public void setUpload(String upload) { this.upload = upload; }

    protected RowDateItem(Parcel in) {

        remInt = in.readString();
        catID = in.readString();
        Category = in.readString();
        categoryArchive = in.readString();
        catDesc = in.readString();
        Reminder = in.readString();
        reminderArchive = in.readString();
        remDate = in.readString();
        remDateInt = in.readInt();
        userID = in.readString();
        upload = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(remInt);
        dest.writeString(catID);
        dest.writeString(Category);
        dest.writeString(categoryArchive);
        dest.writeString(catDesc);
        dest.writeString(Reminder);
        dest.writeString(reminderArchive);
        dest.writeString(remDate);
        dest.writeInt(remDateInt);
        dest.writeString(userID);
        dest.writeString(upload);

    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RowDateItem> CREATOR = new Parcelable.Creator<RowDateItem>() {
        @Override
        public RowDateItem createFromParcel(Parcel in) {
            return new RowDateItem(in);
        }

        @Override
        public RowDateItem[] newArray(int size) {
            return new RowDateItem[size];
        }
    };

}
