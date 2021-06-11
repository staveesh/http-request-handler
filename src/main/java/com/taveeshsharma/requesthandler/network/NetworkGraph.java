package com.taveeshsharma.requesthandler.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class NetworkGraph {
    private Integer numAccessPoints;
    private Integer numMeasurementNodes;
    private Double edgeProbability;
    private Graph<NetworkNode, DefaultEdge> topology;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime addedAt;

    @JsonCreator
    public NetworkGraph() {
    }

    public NetworkGraph(Integer numAccessPoints, Integer numMeasurementNodes, Double edgeProbability) {
        this.numAccessPoints = numAccessPoints;
        this.numMeasurementNodes = numMeasurementNodes;
        this.edgeProbability = edgeProbability;
    }

    public void buildGraph() {
        topology = GraphTypeBuilder.undirected()
                .weighted(false).allowingMultipleEdges(true).allowingSelfLoops(false)
                .vertexSupplier(new NetworkVertexSupplier())
                .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();
        List<NetworkNode> accessPoints = new ArrayList<>();
        List<NetworkNode> measurementNodes = new ArrayList<>();
        for (int idx = 1; idx <= numAccessPoints; idx++) {
            NetworkNode accessPoint = new NetworkNode(false, "AP" + idx, "red", false, 0);
            accessPoints.add(accessPoint);
            topology.addVertex(accessPoint);
        }
        for (int idx = 1; idx <= numMeasurementNodes; idx++) {
            NetworkNode mNode = new NetworkNode(true, "M" + idx,"blue", false, 0);
            measurementNodes.add(mNode);
            topology.addVertex(mNode);
        }
        // Access point to access point
        for (int idx1 = 0; idx1 < accessPoints.size()-1; idx1++) {
            NetworkNode ap1 = accessPoints.get(idx1);
            for (int idx2 = idx1+1; idx2 < accessPoints.size(); idx2++) {
                NetworkNode ap2 = accessPoints.get(idx2);
                if (Math.random() <= edgeProbability) {
                    topology.addEdge(ap1, ap2);
                }
            }
        }
        // Measurement nodes to access points
        for (NetworkNode mNode : measurementNodes) {
            int ap = (int) (Math.random() * numAccessPoints);
            topology.addEdge(mNode, accessPoints.get(ap));
        }
    }

    @JsonProperty("numAccessPoints")
    public Integer getNumAccessPoints() {
        return numAccessPoints;
    }

    public void setNumAccessPoints(Integer numAccessPoints) {
        this.numAccessPoints = numAccessPoints;
    }

    @JsonProperty("numMeasurementNodes")
    public Integer getNumMeasurementNodes() {
        return numMeasurementNodes;
    }

    public void setNumMeasurementNodes(Integer numMeasurementNodes) {
        this.numMeasurementNodes = numMeasurementNodes;
    }

    @JsonProperty("edgeProbability")
    public Double getEdgeProbability() {
        return edgeProbability;
    }

    public void setEdgeProbability(Double edgeProbability) {
        this.edgeProbability = edgeProbability;
    }

    @JsonProperty("topology")
    public Graph<NetworkNode, DefaultEdge> getTopology() {
        return topology;
    }

    public void setTopology(Graph<NetworkNode, DefaultEdge> topology) {
        this.topology = topology;
    }

    @JsonProperty("addedAt")
    public ZonedDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(ZonedDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "NetworkGraph{" +
                "numAccessPoints=" + numAccessPoints +
                ", numMeasurementNodes=" + numMeasurementNodes +
                ", edgeProbability=" + edgeProbability +
                ", topology=" + topology +
                '}';
    }

    public static class NetworkVertexSupplier implements Supplier<NetworkNode> {

        @Override
        public NetworkNode get() {
            return new NetworkNode(false, "dummy", "dummy", false, 0);
        }

    }
}
