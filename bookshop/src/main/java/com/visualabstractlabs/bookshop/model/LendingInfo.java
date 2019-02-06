package com.visualabstractlabs.bookshop.model;

import java.time.LocalDate;

import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class LendingInfo {

	private String lendingId;

	private String userId;

	private String lendedBookId;

	private LocalDate lendedDate;

	private int lendingPeriodInDays;

	private double lendingCharges;

	public LendingInfo() {}


	public LendingInfo(String userId, String lendedBookId, int lendingPeriodInDays,
			double lendingCharges) {
		this.lendingId = BookshopUtils.getUUID();
		this.userId = userId;
		this.lendedBookId = lendedBookId;
		this.lendingPeriodInDays = lendingPeriodInDays;
		this.lendingCharges = lendingCharges;
	}

	public String getLendingId() {
		return lendingId;
	}


	public void setLendingId(String lendingId) {
		this.lendingId = lendingId;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLendedBookId() {
		return lendedBookId;
	}


	public void setLendedBookId(String lendedBookId) {
		this.lendedBookId = lendedBookId;
	}


	public LocalDate getLendedDate() {
		return lendedDate;
	}


	public void setLendedDate(LocalDate lendedDate) {
		this.lendedDate = lendedDate;
	}


	public int getLendingPeriodInDays() {
		return lendingPeriodInDays;
	}

	public void setLendingPeriodInDays(int lendingPeriodInDays) {
		this.lendingPeriodInDays = lendingPeriodInDays;
	}

	public double getLendingCharges() {
		return lendingCharges;
	}

	public void setLendingCharges(double lendingCharges) {
		this.lendingCharges = lendingCharges;
	}


	@Override
	public String toString() {
		return "LendingInfo [lendingId=" + lendingId + ", userId=" + userId + ", lendedBookId="
				+ lendedBookId + ", lendedDate=" + lendedDate + ", lendingPeriodInDays="
				+ lendingPeriodInDays + ", lendingCharges=" + lendingCharges + "]";
	}



}
