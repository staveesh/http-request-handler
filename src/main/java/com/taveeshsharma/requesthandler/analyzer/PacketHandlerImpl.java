package com.taveeshsharma.requesthandler.analyzer;

import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.measurements.PcapMeasurements;
import io.pkts.PacketHandler;
import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketHandlerImpl implements PacketHandler {

    private boolean handlesAllPackets;
    private List<PcapMeasurements> pcapData;
    private DatabaseManager dbManager;
    /**
     * We are only worried about tcp data from the packets for now so the below are to keep track of the tcp addresses.
     */
    private HashMap<String, Integer> srcAddress, destAddress;//, tcpProtocol;

    PacketHandlerImpl(DatabaseManager dbManager) {
        this(true);
        this.dbManager = dbManager;
        pcapData = new ArrayList<>();
    }

    private PacketHandlerImpl(boolean h) {
        this.handlesAllPackets = h;
        srcAddress = new HashMap<>();
        destAddress = new HashMap<>();
    }

    @Override
    public boolean nextPacket(Packet packet) throws IOException {
        //get the source;
        //get the destination
        //get the protocol
//        if (packet.getParentPacket()!=null&&seenParentPackets.add(packet.getParentPacket())) {//if we can't add the parent then we have seen a packet that has the same information so we can just ignore the packets
        if (packet.hasProtocol(Protocol.IPv4)) {
            IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
            long time = iPv4Packet.getArrivalTime();
            String destAdd = iPv4Packet.getDestinationIP();
            String srcIP = iPv4Packet.getSourceIP();
            pcapData.add(new PcapMeasurements(time, destAdd, srcIP));
            System.out.println("Source: "+srcIP+ "Dest: "+destAdd);
            //now update counts in the hash maps
            this.destAddress.put(destAdd, this.destAddress.getOrDefault(destAdd, 0) + 1);
            this.srcAddress.put(srcIP, this.srcAddress.getOrDefault(srcIP, 0) + 1);
        }
//        }
        writeToDB();
        return handlesAllPackets;
    }


    List<String> getTopTenDest() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(destAddress.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<String> topTen = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            topTen.add(entry.getKey());
            if (topTen.size() > 10)
                break;
        }
        return topTen;
    }

    private void writeToDB(){
        dbManager.writePcapData(pcapData);
        pcapData.clear();
    }
    public void clearList(){
        this.destAddress.clear();
        this.srcAddress.clear();
        this.getTopTenDest();
    }
}
