package com.cybertek.entity;

import com.cybertek.entity.common.UserPrincipal;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Component
public class BaseEntityListener extends AuditingEntityListener {

    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @PrePersist
    public void prePersist(BaseEntity baseEntity) {

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

        baseEntity.setLastUpdateDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateUserId(1L);

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            long id = Long.parseLong(authentication.getName());
            baseEntity.setLastUpdateUserId(id);
        }

    }
}
