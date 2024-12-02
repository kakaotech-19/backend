package com.heartsave.todaktodak_api.diary.repository;

import com.heartsave.todaktodak_api.diary.domain.DiaryPageIndex;
import com.heartsave.todaktodak_api.diary.entity.PublicDiaryEntity;
import com.heartsave.todaktodak_api.diary.entity.projection.DiaryPageIndexProjection;
import com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryContentProjection;
import com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryPreviewProjection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MySharedDiaryRepository extends JpaRepository<PublicDiaryEntity, Long> {

  Optional<DiaryPageIndexProjection> findFirstByMemberEntity_IdOrderByCreatedTimeDescIdDesc(
      @Param("memberId") Long memberId);

  @Query(
      value =
          """
                      SELECT
                       new com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryPreviewProjection(
                       pd.id,
                       d.webtoonImageUrl,
                       pd.createdTime
                       )
                      FROM PublicDiaryEntity pd JOIN pd.diaryEntity d
                      WHERE pd.memberEntity.id = :memberId AND pd.createdTime <= :#{#index.createdTime} AND pd.id < :#{#index.publicDiaryId}
                      ORDER BY pd.createdTime DESC, pd.id DESC
          """)
  List<MySharedDiaryPreviewProjection> findNextPreviews(
      @Param("memberId") Long memberId, @Param("index") DiaryPageIndex index, Pageable pageable);

  @Query(
      """
            SELECT new com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryContentProjection(
                pd.id,
                d.id,
                pd.publicContent,
                d.webtoonImageUrl,
                d.bgmUrl,
                d.diaryCreatedTime
            )
            FROM PublicDiaryEntity pd
            JOIN pd.diaryEntity d
            WHERE  pd.memberEntity.id = :memberId AND CAST(d.diaryCreatedTime AS LocalDate) = :publicDiaryDate
            """)
  Optional<MySharedDiaryContentProjection> findContent(
      @Param("memberId") Long memberId, @Param("publicDiaryDate") LocalDate publicDiaryDate);
}
