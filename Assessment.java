package com.wgu.termtracker;

import java.util.Date;

public class Assessment {

   private String title;
   private String note;
   private String perfAssess;
   private String objAssess;
   private Date startDate;
   private Date endDate;

   public Assessment(){}

   public Assessment(String title, String note, String perfAssess, String objAssess, Date startDate, Date endDate){
       this.title = title;
       this.note = note;
       this.perfAssess = perfAssess;
       this.objAssess = objAssess;
       this.startDate = startDate;
       this.endDate = endDate;
   }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPerfAssess() {
        return perfAssess;
    }

    public void setPerfAssess(String perfAssess) {
        this.perfAssess = perfAssess;
    }

    public String getObjAssess() {
        return objAssess;
    }

    public void setObjAssess(String objAssess) {
        this.objAssess = objAssess;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
