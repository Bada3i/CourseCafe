package om.sas.coursecafe.view.model;

import java.io.Serializable;

public class PaymentModel implements Serializable {

    private String paymentId;
    private String paymentCourseId;
    private String paymentDetails;
    private String paymentOwnerId;
    private String paymentCourseTitle;
    private String paymentAmount;
    private String offerPayment;


    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCourseId() {
        return paymentCourseId;
    }

    public void setPaymentCourseId(String paymentCourseId) {
        this.paymentCourseId = paymentCourseId;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getPaymentOwnerId() {
        return paymentOwnerId;
    }

    public void setPaymentOwnerId(String paymentOwnerId) {
        this.paymentOwnerId = paymentOwnerId;
    }

    public String getPaymentCourseTitle() {
        return paymentCourseTitle;
    }

    public void setPaymentCourseTitle(String paymentCourseTitle) {
        this.paymentCourseTitle = paymentCourseTitle;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getOfferPayment() {
        return offerPayment;
    }

    public void setOfferPayment(String offerPayment) {
        this.offerPayment = offerPayment;
    }
}
