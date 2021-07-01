package com.taveeshsharma.requesthandler.dto.documents;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("dns_resolvers")
public class DNSResolver {
    private String address;
    private Double cost;

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
        return "DNSResolver{" +
                "address='" + address + '\'' +
                ", cost=" + cost +
                '}';
    }
}
