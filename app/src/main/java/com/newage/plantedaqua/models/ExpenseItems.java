package com.newage.plantedaqua.models;

public class ExpenseItems {

    private String expenseDate;
    private String expenseTankName;
    private String expenseItemName;
    private String itemID;
    private String mode;
    private int expenseQuantity;
    private Double expensePrice;
    private Double expenseTotalPrice;
    private int expenseMonth;
    private int expenseDay;
    private int expenseYear;
    private boolean showDeleteButton;
    private String category;


    public ExpenseItems() {
        expenseDate = "";
        expenseTankName = "";
        expenseItemName = "";
        expenseQuantity = 0;
        expensePrice = 0d;
        expenseMonth = 0;
        expenseDay = 0;
        expenseYear = 0;
        showDeleteButton = false;
        category = "";
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getExpenseTotalPrice() {
        return expenseTotalPrice;
    }

    public void setExpenseTotalPrice(Double expenseTotalPrice) {
        this.expenseTotalPrice = expenseTotalPrice;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getExpenseMonth() {
        return expenseMonth;
    }

    public int getExpenseDay() {
        return expenseDay;
    }

    public int getExpenseYear() {
        return expenseYear;
    }

    public void setExpenseYear(int expenseYear) {
        this.expenseYear = expenseYear;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseTankName() {
        return expenseTankName;
    }

    public void setExpenseTankName(String expenseTankName) {
        this.expenseTankName = expenseTankName;
    }

    public String getExpenseItemName() {
        return expenseItemName;
    }

    public void setExpenseItemName(String expenseItemName) {
        this.expenseItemName = expenseItemName;
    }

    public int getExpenseQuantity() {
        return expenseQuantity;
    }

    public void setExpenseQuantity(int expenseQuantity) {
        this.expenseQuantity = expenseQuantity;
    }

    public Double getExpensePrice() {
        return expensePrice;
    }

    public void setExpensePrice(Double expensePrice) {
        this.expensePrice = expensePrice;
    }


    public void setExpenseMonth(int expenseMonth) {
        this.expenseMonth = expenseMonth;
    }

    public void setExpenseDay(int expenseDay) {
        this.expenseDay = expenseDay;
    }
}

