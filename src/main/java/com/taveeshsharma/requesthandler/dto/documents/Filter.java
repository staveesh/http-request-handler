package com.taveeshsharma.requesthandler.dto.documents;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("filters")
public class Filter {
    private FilterType type;
    private String address;
    private Double cost;

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "type=" + type +
                ", address='" + address + '\'' +
                ", cost=" + cost +
                '}';
    }
}

enum FilterType{
    SECURITY,
    ADULT,
    FAMILY,
    ADS
}