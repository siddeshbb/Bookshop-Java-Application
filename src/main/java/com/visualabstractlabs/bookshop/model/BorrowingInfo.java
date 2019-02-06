package com.visualabstractlabs.bookshop.model;

import java.time.LocalDate;

public class BorrowingInfo {

	private String borrowingId;

	private String userId;

	private Book borrowedBook;

	private LocalDate borrowedDate;

	private LocalDate returnedDate;

	private int borrowingPeriodInDays;

	private double borrowingCharges;

	private double finePaid;

	private boolean isProcessed;

	public String getBorrowingId() {
		return borrowingId;
	}

	public void setBorrowingId(String borrowingId) {
		this.borrowingId = borrowingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Book getBorrowedBook() {
		return borrowedBook;
	}

	public void setBorrowedBook(Book borrowedBook) {
		this.borrowedBook = borrowedBook;
	}

	public LocalDate getBorrowedDate() {
		return borrowedDate;
	}

	public void setBorrowedDate(LocalDate borrowedDate) {
		this.borrowedDate = borrowedDate;
	}

	public int getBorrowingPeriodInDays() {
		return borrowingPeriodInDays;
	}

	public void setBorrowingPeriodInDays(int borrowingPeriodInDays) {
		this.borrowingPeriodInDays = borrowingPeriodInDays;
	}

	public LocalDate getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(LocalDate returnedDate) {
		this.returnedDate = returnedDate;
	}

	public double getBorrowingCharges() {
		return borrowingCharges;
	}

	public void setBorrowingCharges(double borrowingCharges) {
		this.borrowingCharges = borrowingCharges;
	}


	public double getFinePaid() {
		return finePaid;
	}

	public void setFinePaid(double finePaid) {
		this.finePaid = finePaid;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	@Override
	public String toString() {
		return "BorrowingInfo [borrowingId=" + borrowingId + ", userId=" + userId
				+ ", borrowedBook=" + borrowedBook.toString() + ", borrowedDate=" + borrowedDate
				+ ", returnedDate=" + returnedDate + ", borrowingPeriodInDays="
				+ borrowingPeriodInDays + ", borrowingCharges=" + borrowingCharges + ", finePaid="
				+ finePaid + ", isProcessed=" + isProcessed + "]";
	}


}
