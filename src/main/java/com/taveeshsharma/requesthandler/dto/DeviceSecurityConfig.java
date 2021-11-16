package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import com.taveeshsharma.requesthandler.dto.documents.Filter;
import com.taveeshsharma.requesthandler.dto.documents.VpnServer;

public class DeviceSecurityConfig {
    private Filter filter;
    private VpnServer vpn;

    public DeviceSecurityConfig(Filter filter, VpnServer vpn) {
        this.filter = filter;
        this.vpn = vpn;
    }

    @JsonProperty("filter")
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @JsonProperty("vpn")
    public VpnServer getVpn() {
        return vpn;
    }

    public void setVpn(VpnServer vpn) {
        this.vpn = vpn;
    }
}
