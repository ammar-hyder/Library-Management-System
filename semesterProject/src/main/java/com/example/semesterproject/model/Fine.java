package com.example.semesterproject.model;

import java.time.LocalDate;

public class Fine {

    private int fine_Id;
    private int borrowing_Id;
    private double fine_Amount;
    private LocalDate fine_Due_Date;
    private String fine_Status;

    // Constructor


    // Getters and Setters
    public int getFineId() {
        return fine_Id;
    }

    public void setFineId(int fineId) {
        this.fine_Id = fineId;
    }

    public int getBorrowing_Id() {
        return borrowing_Id;
    }

    public void setBorrowing_Id(int borrowing_Id) {
        this.borrowing_Id = borrowing_Id;
    }

    public double getFineAmount() {
        return fine_Amount;
    }

    public void setFineAmount(double fineAmount) {
        this.fine_Amount = fineAmount;
    }

    public LocalDate getFineDueDate() {
        return fine_Due_Date;
    }

    public void setFineDueDate(LocalDate fineDueDate) {
        this.fine_Due_Date = fineDueDate;
    }

    public String getFineStatus() {
        return fine_Status;
    }

    public void setFineStatus(String fineStatus) {
        this.fine_Status = fineStatus;
    }


}
