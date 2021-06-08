package com.taveeshsharma.requesthandler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taveeshsharma.requesthandler.network.NetworkGraph;
import com.taveeshsharma.requesthandler.network.NetworkNode;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class InitialDataConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataConfiguration.class);

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() {
        Resource sourceData = new ClassPathResource("roles.json");
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{sourceData});
        return factory;
    }

    @Bean
    public NetworkGraph getNetworkGraph() throws IOException {
        NetworkGraph graph = new NetworkGraph(6,8,0.1);
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
                logger.info(""+graph.getTopology());
            }
        }
        if(graph.getTopology() == null){
            graph.buildGraph();
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
            logger.info(writer.toString());
            fw.write(writer.toString());
            fw.close();
        }
        return graph;
    }
}
