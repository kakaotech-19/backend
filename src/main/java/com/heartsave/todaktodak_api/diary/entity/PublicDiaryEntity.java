package com.heartsave.todaktodak_api.diary.entity;

import static com.heartsave.todaktodak_api.diary.constant.DiaryContentConstraintConstant.PUBLIC_DIARY_CONTENT_MAX_SIZE;

import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
    name = "PUBLIC_DIARY_SEQ_GENERATOR",
    sequenceName = "PUBLIC_DIARY_SEQ",
    initialValue = 1,
    allocationSize = 50)
@Table(name = "public_diary")
public class PublicDiaryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PUBLIC_DIARY_SEQ_GENERATOR")
  @Column(updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity memberEntity;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_id", nullable = false)
  private DiaryEntity diaryEntity;

  @Size(max = PUBLIC_DIARY_CONTENT_MAX_SIZE)
  @Column(name = "public_content", nullable = true, columnDefinition = "text")
  private String publicContent;
}