package com.google.cloud.android.speech;

/**
 * Created by ASUS on 07/06/2019.
 */

public class Product {
    private String pName;
    private String pPrice;
    private String pUrlThumbnail;
    private String pSNo;
    private String Image;
    private String pcategory;


    public Product() {
    }


    public Product(String pName, String pcategory, String pPrice, String pUrlThumbnail, String pSNo, String Image) {
        this.pName = pName;
        this.pPrice = pPrice;
        this.pUrlThumbnail = pUrlThumbnail;
        this.pSNo = pSNo;
        this.Image= Image;
        this.pcategory= pcategory;

    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpPrice() {
        return pPrice;
    }


    public void setCategory(String category) {
        this.pcategory= category;
    }
    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }
    public String getpUrlThumbnail() {
        return pUrlThumbnail;
    }

    public void setpUrlThumbnail(String pUrlThumbnail) {
        this.pUrlThumbnail = pUrlThumbnail;
    }

    public String getpSNo() {
        return pSNo;
    }

    public void setpSNo(String pSNo) {
        this.pSNo = pSNo;
    }

    @Override
    public String toString() {
        return "Sno:" + pSNo +
                " Name " + pName +
                " Price " + pPrice +
                " UrlThumbnail " + pUrlThumbnail;
    }

}
