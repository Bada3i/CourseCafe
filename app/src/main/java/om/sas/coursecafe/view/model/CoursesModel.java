package om.sas.coursecafe.view.model;


import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class CoursesModel implements Serializable {

    private String paymentAmount;
    private String postId;
    private String title;
    private String image;
    private String institution;
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
    private String email ;
    private String phone;
    private String postProviderId;
    private double location_lat;
    private double location_lng;
    private String locationAddress;
    private String postProviderImage;
    private String postProviderName;
    private String postSubmitTime;
    private String offerType;
    private String offerDetails;
    private String finalPayment;
    private int report;
    private AudienceContainer myAudiance;
    private String locationType;




    public CoursesModel(String postId, String title, String image, String institution, String startTime, String endTime, String startDate, String endDate, String email, String phone, String postProviderId, double location_lat, double location_lng, String locationAddress, String postProviderImage, String postProviderName, String postSubmitTime, int report) {

        this.postId = postId;
        this.title = title;
        this.image = image;
        this.institution = institution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.email = email;
        this.phone = phone;
        this.postProviderId = postProviderId;
        this.location_lat = location_lat;
        this.location_lng = location_lng;
        this.locationAddress = locationAddress;
        this.postProviderImage = postProviderImage;
        this.postProviderName = postProviderName;
        this.postSubmitTime = postSubmitTime;
        this.report = report;

    }

    public CoursesModel(String postId) {
        this.postId = postId;
    }



    public CoursesModel() {
    }


    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getPostProviderId() {
        return postProviderId;
    }

    public void setPostProviderId(String postProviderId) {
        this.postProviderId = postProviderId;
    }

    public double getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(double location_lat) {
        this.location_lat = location_lat;
    }

    public double getLocation_lng() {
        return location_lng;
    }

    public void setLocation_lng(double location_lng) {
        this.location_lng = location_lng;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getPostProviderImage() {
        return postProviderImage;
    }

    public void setPostProviderImage(String postProviderImage) {
        this.postProviderImage = postProviderImage;
    }

    public String getPostProviderName() {
        return postProviderName;
    }

    public void setPostProviderName(String postProviderName) {
        this.postProviderName = postProviderName;
    }

    public String getPostSubmitTime() {
        return postSubmitTime;
    }

    public void setPostSubmitTime(String postSubmitTime) {
        this.postSubmitTime = postSubmitTime;
    }



    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public AudienceContainer getMyAudiance() {
        return myAudiance;
    }

    public void setMyAudiance(AudienceContainer myAudiance) {
        this.myAudiance = myAudiance;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getFinalPayment() {
        return finalPayment;
    }

    public void setFinalPayment(String finalPayment) {
        this.finalPayment = finalPayment;
    }
}
