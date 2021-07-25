package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CiphersRepository extends MongoRepository<Cipher, String>, CiphersRepositoryCustom {
}
