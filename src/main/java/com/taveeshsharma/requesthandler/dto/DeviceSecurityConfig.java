package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import com.taveeshsharma.requesthandler.dto.documents.Filter;

public class DeviceSecurityConfig {
    private Filter filter;
    private Cipher cipher;

    public DeviceSecurityConfig(Filter filter, Cipher cipher) {
        this.filter = filter;
        this.cipher = cipher;
    }

    @JsonProperty("filter")
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    @JsonProperty("cipher")
    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
