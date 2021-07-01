package com.taveeshsharma.requesthandler.dto.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("security_settings")
public class DeviceSecuritySettings {
    @Id
    private String deviceId;
    private String strength; // ["high", "low", "medium"]
    @DBRef
    private List<DNSResolver> dnsResolvers;
    private List<String> ciphers;
    @DBRef
    private List<Filter> filters;
    private String vpn;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public List<DNSResolver> getDnsResolvers() {
        return dnsResolvers;
    }

    public void setDnsResolvers(List<DNSResolver> dnsResolvers) {
        this.dnsResolvers = dnsResolvers;
    }

    public List<String> getCiphers() {
        return ciphers;
    }

    public void setCiphers(List<String> ciphers) {
        this.ciphers = ciphers;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public String getVpn() {
        return vpn;
    }

    public void setVpn(String vpn) {
        this.vpn = vpn;
    }
}
