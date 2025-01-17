package com.heartsave.todaktodak_api.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.heartsave.todaktodak_api.common.BaseTestObject;
import com.heartsave.todaktodak_api.diary.constant.DiaryReactionType;
import com.heartsave.todaktodak_api.diary.entity.DiaryEntity;
import com.heartsave.todaktodak_api.diary.entity.DiaryReactionEntity;
import com.heartsave.todaktodak_api.diary.entity.PublicDiaryEntity;
import com.heartsave.todaktodak_api.diary.entity.projection.DiaryReactionCountProjection;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@Slf4j
@DataJpaTest
public class DiaryReactionRepositoryTest {

  @Autowired private DiaryReactionRepository diaryReactionRepository;
  @Autowired private TestEntityManager tem; // Reaction 을 제외한 repository 구문 대체를 위한 객체

  private MemberEntity member;
  private DiaryEntity diary;
  private PublicDiaryEntity publicDiary;

  @BeforeEach
  void setupAll() {
    member = BaseTestObject.createMemberNoId();
    diary = BaseTestObject.createDiaryNoIdWithMember(member);
    publicDiary =
        PublicDiaryEntity.builder()
            .memberEntity(member)
            .diaryEntity(diary)
            .publicContent("public-content")
            .build();
    tem.persist(member);
    tem.persist(diary);
    tem.persist(publicDiary);
    tem.flush();
    tem.clear();
  }

  @Test
  @DisplayName("findReactionCountById - 반응이 있는 경우 성공적으로 조회")
  void findReactionCountByIdSuccess() {
    Long expectedLikes = 1L;
    Long expectedSurprised = 3L;
    Long expectedEmpathize = 14L;
    Long expectedCheering = 100L;
    MemberEntity testMember = BaseTestObject.createMemberNoId();
    tem.persist(testMember);
    // LIKE 1개 생성
    diaryReactionRepository.save(
        DiaryReactionEntity.builder()
            .memberEntity(testMember)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build());

    // SURPRISED 3개 생성
    for (int i = 0; i < expectedSurprised; i++) {
      testMember = BaseTestObject.createMemberNoId();
      tem.persist(testMember);
      diaryReactionRepository.save(
          DiaryReactionEntity.builder()
              .memberEntity(testMember)
              .publicDiaryEntity(publicDiary)
              .reactionType(DiaryReactionType.SURPRISED)
              .build());
    }

    // EMPATHIZE 14개 생성
    for (int i = 0; i < expectedEmpathize; i++) {
      testMember = BaseTestObject.createMemberNoId();
      tem.persist(testMember);
      diaryReactionRepository.save(
          DiaryReactionEntity.builder()
              .memberEntity(testMember)
              .publicDiaryEntity(publicDiary)
              .reactionType(DiaryReactionType.EMPATHIZE)
              .build());
    }

    // CHEERING 100개 생성
    for (int i = 0; i < expectedCheering; i++) {
      testMember = BaseTestObject.createMemberNoId();
      tem.persist(testMember);
      diaryReactionRepository.save(
          DiaryReactionEntity.builder()
              .memberEntity(testMember)
              .publicDiaryEntity(publicDiary)
              .reactionType(DiaryReactionType.CHEERING)
              .build());
    }

    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());

    assertThat(result).isNotNull();
    DiaryReactionCountProjection count = result;
    assertThat(count.getLikes()).as("좋아요 수가 예상값과 다릅니다.").isEqualTo(expectedLikes);
    assertThat(count.getSurprised()).as("놀라워요 수가 예상값과 다릅니다.").isEqualTo(expectedSurprised);
    assertThat(count.getEmpathize()).as("공감해요 수가 예상값과 다릅니다.").isEqualTo(expectedEmpathize);
    assertThat(count.getCheering()).as("응원해요 수가 예상값과 다릅니다.").isEqualTo(expectedCheering);
  }

  @Test
  @DisplayName("findReactionCountById - 반응이 없는 경우")
  void findReactionCountByIdEmpty() {
    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());

    assertThat(result).isNotNull();
    DiaryReactionCountProjection count = result;
    assertThat(count.getLikes()).as("좋아요 수가 0이 아닙니다.").isEqualTo(0L);
    assertThat(count.getSurprised()).as("놀라워요 수가 0이 아닙니다.").isEqualTo(0L);
    assertThat(count.getEmpathize()).as("공감해요 수가 0이 아닙니다.").isEqualTo(0L);
    assertThat(count.getCheering()).as("응원해요 수가 0이 아닙니다.").isEqualTo(0L);
  }

  @Test
  @DisplayName("findReactionCountById - 일기가 없는 경우")
  void findReactionCountByIdWithNoDiary() {
    DiaryReactionEntity reaction1 =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();
    DiaryReactionEntity reaction2 =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.CHEERING)
            .build();

    diaryReactionRepository.save(reaction1);
    diaryReactionRepository.save(reaction2);

    tem.flush();
    tem.clear();

    System.out.println("diary.getDiaryCreatedTime() = " + diary.getDiaryCreatedTime());
    diary = tem.find(DiaryEntity.class, diary.getId());
    System.out.println("diary.getDiaryCreatedTime() = " + diary.getDiaryCreatedTime());
    tem.remove(diary);

    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(publicDiary.getId());

    assertThat(result.getLikes()).as("삭제된 일기의 반응 수가 조회되었습니다.").isEqualTo(0);
    assertThat(result.getCheering()).as("삭제된 일기의 반응 수가 조회되었습니다.").isEqualTo(0);
    assertThat(result.getEmpathize()).as("삭제된 일기의 반응 수가 조회되었습니다.").isEqualTo(0);
    assertThat(result.getSurprised()).as("삭제된 일기의 반응 수가 조회되었습니다.").isEqualTo(0);
  }

  @Test
  @DisplayName("toggleReactionStatus - 같은 멤버가 같은 일기에 같은 타입의 반응을 하면 삭제됨")
  void toggleReactionStatusTest() {
    DiaryReactionEntity reaction =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();

    diaryReactionRepository.save(reaction);

    DiaryReactionCountProjection resultAfterSave =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());
    assertThat(resultAfterSave.getLikes()).as("반응이 저장되지 않았습니다.").isEqualTo(1);

    diaryReactionRepository.deleteReaction(member.getId(), diary.getId(), DiaryReactionType.LIKE);

    DiaryReactionCountProjection resultAfterDelete =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());
    assertThat(resultAfterDelete.getLikes()).as("반응이 삭제되지 않았습니다.").isEqualTo(0);
  }

  @Test
  @DisplayName("toggleReactionStatus - 같은 멤버가 같은 일기에 다른 타입의 반응을 하면 모두 저장됨")
  void toggleDifferentReactionTypeTest() {
    DiaryReactionEntity likeReaction =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();

    DiaryReactionEntity cheeringReaction =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.CHEERING)
            .build();

    diaryReactionRepository.save(likeReaction);
    diaryReactionRepository.save(cheeringReaction);

    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());
    assertThat(result.getLikes()).as("LIKE 반응이 저장되지 않았습니다.").isEqualTo(1);
    assertThat(result.getCheering()).as("CHEERING 반응이 저장되지 않았습니다.").isEqualTo(1);
  }

  @Test
  @DisplayName("toggleReactionStatus - 다른 멤버가 같은 일기에 같은 타입의 반응을 하면 모두 저장됨")
  void toggleSameReactionTypeDifferentMemberTest() {
    MemberEntity anotherMember = BaseTestObject.createMemberNoId();
    tem.persist(anotherMember);

    DiaryReactionEntity reaction1 =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();

    DiaryReactionEntity reaction2 =
        DiaryReactionEntity.builder()
            .memberEntity(anotherMember)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();

    diaryReactionRepository.save(reaction1);
    diaryReactionRepository.save(reaction2);

    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(diary.getId());
    assertThat(result.getLikes()).as("두 멤버의 LIKE 반응이 모두 저장되지 않았습니다.").isEqualTo(2);
  }

  @Test
  @DisplayName("toggleReactionStatus - 같은 멤버가 같은 일기에 같은 타입의 반응을 중복 저장할 경우 예외 발생")
  void toggleReactionStatusDuplicateTest() {
    DiaryReactionEntity reaction =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();
    diaryReactionRepository.save(reaction);

    tem.flush();

    DiaryReactionEntity duplicateReaction =
        DiaryReactionEntity.builder()
            .memberEntity(member)
            .publicDiaryEntity(publicDiary)
            .reactionType(DiaryReactionType.LIKE)
            .build();
    System.out.println("reaction = " + reaction);
    System.out.println("duplicateReaction = " + duplicateReaction);
    assertThatThrownBy(
            () -> {
              diaryReactionRepository.save(duplicateReaction);
              tem.flush();
            },
            "제약조건 위반 예외가 발생해야 합니다.")
        .isInstanceOf(ConstraintViolationException.class);
    tem.clear(); // duplicateReaction clear
    DiaryReactionCountProjection result =
        diaryReactionRepository.countEachByPublicDiaryId(publicDiary.getId());
    Assertions.assertThat(result.getLikes()).as("기존 반응이 유지되어야 합니다.").isEqualTo(1);
  }
}
