package com.taveeshsharma.requesthandler.network;

public class NetworkNode {
    private boolean isMeasurementNode;
    private String label;
    private String color;
    private boolean isGatewayRouter;
    // Only applicable if isMeasurementNode = true
    private Integer cost;

    public NetworkNode(boolean isMeasurementNode, String label, String color, boolean isGatewayRouter, Integer cost) {
        this.isMeasurementNode = isMeasurementNode;
        this.label = label;
        this.color = color;
        this.isGatewayRouter = isGatewayRouter;
        this.cost = cost;
    }

    public boolean isMeasurementNode() {
        return isMeasurementNode;
    }

    public void setMeasurementNode(boolean measurementNode) {
        isMeasurementNode = measurementNode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isGatewayRouter() {
        return isGatewayRouter;
    }

    public void setGatewayRouter(boolean gatewayRouter) {
        isGatewayRouter = gatewayRouter;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return label+(isGatewayRouter ? "*" : "")+(isMeasurementNode ? "{"+cost+"}" : "");
    }
}
