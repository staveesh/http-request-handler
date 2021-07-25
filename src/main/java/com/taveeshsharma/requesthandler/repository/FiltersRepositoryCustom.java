package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Filter;

public interface FiltersRepositoryCustom {
    Filter findBestFilter(String networkType, String level);
}
