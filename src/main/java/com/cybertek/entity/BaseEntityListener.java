package com.cybertek.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Component
public class BaseEntityListener extends AuditingEntityListener {


    @PrePersist
    public void prePersist(BaseEntity baseEntity) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setInsertDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateDateTime(LocalDateTime.now());
        baseEntity.setInsertUserId(1L);
        baseEntity.setLastUpdateUserId(1L);

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            long id = Long.parseLong(authentication.getName());
            baseEntity.setInsertUserId(id);
            baseEntity.setLastUpdateUserId(id);
        }

    }

    @PreUpdate
    public void preUpdate(BaseEntity baseEntity) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setLastUpdateDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateUserId(1L);

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            long id = Long.parseLong(authentication.getName());
            baseEntity.setLastUpdateUserId(id);
        }

    }
}
