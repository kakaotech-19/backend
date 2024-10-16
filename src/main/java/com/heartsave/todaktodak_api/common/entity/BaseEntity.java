package com.heartsave.todaktodak_api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(updatable = false)
  @CreatedBy
  private Long createdBy;

  @LastModifiedDate private LocalDateTime updatedAt;

  @LastModifiedBy private Long updatedBy;
}
