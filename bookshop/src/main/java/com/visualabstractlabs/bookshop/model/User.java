package com.visualabstractlabs.bookshop.model;

import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class User {

	private String userID;

	private String name;

	private Address address;

	private String phoneNumber;

	private double totalSpend;

	private MembershipStatus membershipStatus;

	private double earnings;

	public User() {
		this.userID = BookshopUtils.getUUID();
		this.membershipStatus = MembershipStatus.Bronze;
	}

	public User(String userID, String name, Address address, String phoneNumber,
			double totalSpend) {
		this.userID = userID;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.totalSpend = totalSpend;
		this.membershipStatus = MembershipStatus.Bronze;
	}



	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getTotalSpend() {
		return totalSpend;
	}

	public void setTotalSpend(double totalSpend) {
		this.totalSpend = totalSpend;
	}

	public MembershipStatus getMembershipStatus() {
		return membershipStatus;
	}

	public void setMembershipStatus(MembershipStatus membershipStatus) {
		this.membershipStatus = membershipStatus;
	}

	public double getEarnings() {
		return earnings;
	}

	public void setEarnings(double earnings) {
		this.earnings = earnings;
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + ", name=" + name + ", address=" + address.toString()
				+ ", phoneNumber=" + phoneNumber + ", totalSpend=" + totalSpend
				+ ", membershipStatus=" + membershipStatus + ", earnings=" + earnings + "]";
	}



}
