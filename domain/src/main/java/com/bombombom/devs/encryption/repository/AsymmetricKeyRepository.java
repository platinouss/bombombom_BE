package com.bombombom.devs.encryption.repository;

import com.bombombom.devs.encryption.model.AsymmetricKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsymmetricKeyRepository extends JpaRepository<AsymmetricKey, Long> {

}
