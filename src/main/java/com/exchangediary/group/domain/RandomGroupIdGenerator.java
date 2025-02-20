package com.exchangediary.group.domain;

import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RandomGroupIdGenerator implements IdentifierGenerator {
    private final GroupRepository groupRepository;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return generateUniqueGroupId();
    }

    private String generateUniqueGroupId() {
        String randomId;

        do {
            randomId = UUID.randomUUID().toString().substring(0, 8);
        } while (groupRepository.existsById(randomId));
        return randomId;
    }
}
