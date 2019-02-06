package com.visualabstractlabs.bookshop.model;

public class Order {

	private String orderID;

	private String userID;

	private String bookID;

	private int quantity;

	private double amount;

	public Order() {}


	public Order(String orderID, String userID, String bookID, int quantity, double amount) {
		this.orderID = orderID;
		this.userID = userID;
		this.bookID = bookID;
		this.quantity = quantity;
		this.amount = amount;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getBookID() {
		return bookID;
	}

	public void setBookID(String bookID) {
		this.bookID = bookID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}


	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", userID=" + userID + ", bookID=" + bookID
				+ ", quantity=" + quantity + ", amount=" + amount + "]";
	}

}
