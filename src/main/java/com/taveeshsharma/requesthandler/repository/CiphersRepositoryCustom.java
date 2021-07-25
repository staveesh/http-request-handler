package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Cipher;

public interface CiphersRepositoryCustom {
    Cipher findBestCipherByLevel(String level);
}
