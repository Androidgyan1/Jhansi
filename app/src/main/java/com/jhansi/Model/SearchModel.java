package com.jhansi.Model;

import java.io.Serializable;

public class SearchModel implements Serializable {
    private String name, email,textViewownerhousename,textViewownermobile,textViewownerbilldate,isEligibleForDiscount,
            discount_amount,CurrentSurcharge,AmountRecivedCurrentFY,TotalTax;

    public SearchModel(String name, String email,String textViewownerhousename,String textViewownermobile,String textViewownerbilldate,
                       String isEligibleForDiscount,String discount_amount,String CurrentSurcharge,String AmountRecivedCurrentFY,
                       String TotalTax) {
        this.name = name;
        this.email = email;
        this.textViewownerhousename = textViewownerhousename;
        this.textViewownermobile = textViewownermobile;
        this.textViewownerbilldate = textViewownerbilldate;
        this.isEligibleForDiscount = isEligibleForDiscount;
        this.discount_amount = discount_amount;
        this.CurrentSurcharge = CurrentSurcharge;
        this.AmountRecivedCurrentFY = AmountRecivedCurrentFY;
        this.TotalTax = TotalTax;
    }

    public String getJLalKal() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String gethousename() {
        return textViewownerhousename;
    }

    public String getmobileno() {
        return textViewownermobile;
    }

    public String getbilldate() {
        return textViewownerbilldate;
    }

    public String getisEligibleForDiscount() {
        return isEligibleForDiscount;
    }

    public String discount_amount() {
        return discount_amount;
    }

    public String current_surcharge() {
        return CurrentSurcharge;
    }

    public String AmountRecivedCurrentFY() {
        return AmountRecivedCurrentFY;
    }


    public String TaTotalTax() {
        return TotalTax;
    }

}
