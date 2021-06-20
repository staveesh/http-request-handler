package com.taveeshsharma.requesthandler.config;

import com.taveeshsharma.requesthandler.network.NetworkGraph;
import com.taveeshsharma.requesthandler.network.NetworkNode;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.util.SupplierUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class NetworkTopologyConfig {

    private static final Logger logger = LoggerFactory.getLogger(NetworkTopologyConfig.class);

    public NetworkGraph createOrImportGraph() throws IOException {
        NetworkGraph graph = new NetworkGraph(8,4,0.9);
        // TODO: Move this path to properties file
        File topFile = new File("/var/lib/graphs/topology.dot");
        if(topFile.exists()) {
            try (InputStream fStream = new FileInputStream(topFile)) {
                DOTImporter<NetworkNode, DefaultEdge> dotImporter = new DOTImporter<>();
                dotImporter.addVertexAttributeConsumer((p, attrValue) -> {
                    String attrName = p.getSecond();
                    NetworkNode v;
                    String value;
                    switch (attrName) {
                        case "isMeasurementNode":
                            value = attrValue.getValue();
                            v = p.getFirst();
                            v.setMeasurementNode(Boolean.parseBoolean(value));
                            break;
                        case "label": {
                            value = attrValue.getValue();
                            v = p.getFirst();
                            v.setLabel(value);
                            break;
                        }
                        case "color": {
                            value = attrValue.getValue();
                            v = p.getFirst();
                            v.setColor(value);
                            break;
                        }
                        case "isGatewayRouter":
                            value = attrValue.getValue();
                            v = p.getFirst();
                            v.setGatewayRouter(Boolean.parseBoolean(value));
                            break;
                    }
                });
                BufferedReader reader = new BufferedReader(new InputStreamReader(fStream));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                graph.setTopology(GraphTypeBuilder.undirected()
                        .weighted(false).allowingMultipleEdges(true).allowingSelfLoops(false)
                        .vertexSupplier(new NetworkGraph.NetworkVertexSupplier())
                        .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph());
                dotImporter.importGraph(graph.getTopology(), new StringReader(out.toString()));
            }
        }
        return graph;
    }

    public void exportGraph(NetworkGraph graph) throws IOException{
        DOTExporter<NetworkNode, DefaultEdge> exporter =
                new DOTExporter<>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("isMeasurementNode", DefaultAttribute.createAttribute(v.isMeasurementNode()));
            map.put("label", DefaultAttribute.createAttribute(v.getLabel()));
            map.put("color", DefaultAttribute.createAttribute(v.getColor()));
            map.put("isGatewayRouter", DefaultAttribute.createAttribute(v.isGatewayRouter()));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph.getTopology(), writer);
        File file = new File("/var/lib/graphs/topology.dot");
        FileWriter fw;
        if(!file.exists()) {
            file.createNewFile();
        }
        fw = new FileWriter(file);
        fw.write(writer.toString());
        fw.close();
    }

    @Bean
    public List<NetworkNode> getMeasurementNodes() throws IOException {
        NetworkGraph graph = createOrImportGraph();
        if(graph.getTopology() == null){
            graph.buildGraph();
        }
        assignGatewayRouters(graph);
        exportGraph(graph);
        computeCosts(graph);
        List<NetworkNode> costs = graph.getTopology().vertexSet()
                .stream()
                .filter(NetworkNode::isMeasurementNode)
                .sorted(Comparator.comparing(NetworkNode::getCost))
                .collect(Collectors.toList());
        computeAssignmentProbabilities(costs);
        logger.info(costs.stream().map(node -> node.toString()+":"+node.getCost()).collect(Collectors.toList()).toString());
        return costs;
    }

    private void computeAssignmentProbabilities(List<NetworkNode> costs) {
        double idx = 0, m = costs.size();
        for(NetworkNode node : costs){
            if(node.isMeasurementNode()){
                node.setProbAssignment((m-idx)/(m+1));
                idx++;
            }
        }
    }

    public void computeCosts(NetworkGraph graph){
        BiconnectivityInspector<NetworkNode, DefaultEdge> algo = new BiconnectivityInspector<>(graph.getTopology());
        Set<Graph<NetworkNode, DefaultEdge>> connectedComponents = algo.getConnectedComponents();
        for(Graph<NetworkNode, DefaultEdge> cc : connectedComponents) {
            NetworkNode dest = null;
            List<NetworkNode> sources = new ArrayList<>();
            for(NetworkNode node : cc.vertexSet()){
                if(node.isMeasurementNode()){
                    sources.add(node);
                }
                else if(node.isGatewayRouter()){
                    dest = node;
                }
            }
            for(NetworkNode src : sources){
                Set<List<NetworkNode>> paths = findAllPaths(cc, src, dest);
                int cost = 0;
                for(List<NetworkNode> path : paths)
                    cost += (path.size()-1);
                src.setCost(cost);
            }
        }
    }

    public Set<List<NetworkNode>> findAllPaths(Graph<NetworkNode, DefaultEdge> G, NetworkNode src, NetworkNode dst){
        Set<List<NetworkNode>> allPaths = new HashSet<>();
        if(dst == null)
            return allPaths;
        Set<NetworkNode> visited = new HashSet<>();
        List<NetworkNode> path = new ArrayList<>();
        dfs(visited, path, allPaths, G, src, dst);
        return allPaths;
    }

    public void dfs(Set<NetworkNode> visited, List<NetworkNode> path, Set<List<NetworkNode>> allPaths,
                    Graph<NetworkNode, DefaultEdge> G, NetworkNode src, NetworkNode dst){
        if(visited.contains(src))
            return;
        visited.add(src);
        path.add(src);
        if(src.equals(dst)){
            allPaths.add(new ArrayList<>(path));
            visited.remove(src);
            path.remove(path.size()-1);
            return;
        }
        for(NetworkNode neighbor : Graphs.neighborListOf(G, src)){
            dfs(visited, path, allPaths, G, neighbor, dst);
        }
        path.remove(path.size()-1);
        visited.remove(src);
    }

    private void assignGatewayRouters(NetworkGraph graph) {
        // Assign at least one gateway router to each Connected Component
        BiconnectivityInspector<NetworkNode, DefaultEdge> algo = new BiconnectivityInspector<>(graph.getTopology());
        Set<Graph<NetworkNode, DefaultEdge>> connectedComponents = algo.getConnectedComponents();
        for(Graph<NetworkNode, DefaultEdge> cc : connectedComponents){
            boolean noGatewayRouterInCC = true;
            List<NetworkNode> accessPoints = new ArrayList<>();
            for(NetworkNode node : cc.vertexSet()){
                if(!node.isMeasurementNode()) {
                    if(node.isGatewayRouter()) {
                        noGatewayRouterInCC = false;
                        break;
                    }
                    else
                        accessPoints.add(node);
                }
            }
            if(noGatewayRouterInCC){
                accessPoints.get((int) (Math.random()*accessPoints.size())).setGatewayRouter(true);
            }
        }
    }
}
