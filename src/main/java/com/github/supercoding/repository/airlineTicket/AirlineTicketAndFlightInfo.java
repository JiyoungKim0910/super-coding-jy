package com.github.supercoding.repository.airlineTicket;

import java.util.Objects;

public class AirlineTicketAndFlightInfo {
    private Integer ticketId;
    private Integer price;
    private Integer charges;
    private Integer tax;
    private Integer totalPrice;

    public AirlineTicketAndFlightInfo(Integer ticketId, Double price, Double charges, Double tax, Double totalPrice) {
        this.ticketId = ticketId;
        this.price = price.intValue();
        this.charges = charges.intValue();
        this.tax = tax.intValue();
        this.totalPrice = totalPrice.intValue();
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCharges() {
        return charges;
    }

    public void setCharges(Integer charges) {
        this.charges = charges;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AirlineTicketAndFlightInfo that)) return false;
        return Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ticketId);
    }

    @Override
    public String toString() {
        return "AirlineTicketAndFlightInfo{" +
                "ticketId=" + ticketId +
                ", price=" + price +
                ", charges=" + charges +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
