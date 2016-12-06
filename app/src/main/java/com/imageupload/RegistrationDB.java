package com.imageupload;

/**
 * Created by chowd on 05-12-2016.
 */

public class RegistrationDB {
    String emailID,profileImage;

    public RegistrationDB(){}

    public RegistrationDB(String emailID,String profileImage){
        this.emailID=emailID;
        this.profileImage=profileImage;

    }

    public String getEmailID() { return emailID; }

    public void setEmailID(String email) {
        this.emailID = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
