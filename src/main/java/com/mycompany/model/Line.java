
package com.mycompany.model;


public class Line {

    private String item;
    private double price;
    private int count;
    private Invoice invoice;

    public Line(String item, double price, int count, Invoice invoice) {
       
        this.item = item;
        this.price = price;
        this.count = count;
        this.invoice = invoice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    @Override
    public String toString() {
        return "Line{" + "num=" + invoice.getNum() + ", item=" + item + ", price=" + price + ", count=" + count + '}';
    }

    public Line() {
    }

    public Line( String item, double price, int count) {
     
        this.item = item;
        this.price = price;
        this.count = count;
    }

    public double getLineTotal(){
        return price * count;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
      public String getAsCSV()
    {
        return invoice.getNum() + "," + item + "," + price + "," + count;
    }
    
    
}
