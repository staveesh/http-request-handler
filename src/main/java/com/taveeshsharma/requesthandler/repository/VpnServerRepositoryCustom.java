package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.VpnServer;

public interface VpnServerRepositoryCustom {
    VpnServer sortByPingAndSpeed();
}
