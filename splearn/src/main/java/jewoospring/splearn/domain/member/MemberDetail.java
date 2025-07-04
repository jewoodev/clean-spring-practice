package jewoospring.splearn.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jewoospring.splearn.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.util.Assert.isTrue;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class MemberDetail extends AbstractEntity {
    @OneToOne(mappedBy = "detail")
    private Member member;

    @Column(length = 20)
    private Profile profile;

    @Column(length = 50)
    private String introduction;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    static MemberDetail create() {
        var memberDetail = new MemberDetail();

        memberDetail.registeredAt = LocalDateTime.now();

        return memberDetail;
    }

    void whenActivated() {
        isTrue(this.activatedAt == null, "이미 활성화 상태 갱신이 적용되어 갱신에 실패합니다.");
        this.activatedAt = LocalDateTime.now();
    }

    void whenDeactivated() {
        isTrue(this.deactivatedAt == null, "이미 비활성화 상태 갱신이 적용되어 갱신에 실패합니다.");
        this.deactivatedAt = LocalDateTime.now();
    }

    void updateMainInfo(MemberInfoUpdateRequest updateRequest) {
        this.profile = new Profile(updateRequest.profileAddress());

        String introduction = Objects.requireNonNull(updateRequest.introduction(), "'자기 소개글 갱신' 관련한 비이상적인 사용이 감지되었습니다.");
        isTrue(introduction.length() > 5 && introduction.length() < 50, "자기 소개글은 5 ~ 50자 사이의 길이를 가져야 합니다.");
        this.introduction = introduction;
    }
}
