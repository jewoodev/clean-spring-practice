package jewoospring.splearn.domain.member;

import jakarta.persistence.*;
import jewoospring.splearn.domain.AbstractEntity;
import jewoospring.splearn.domain.shared.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.state;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email_address", name = "UK_EMAIL_ADDRESS"),
        @UniqueConstraint(columnNames = "detail_id", name = "UK_DETAIL_ID"),
})
@Getter
@ToString(callSuper = true, exclude = "detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Member extends AbstractEntity {
    @NaturalId
    @Embedded
    @Column(nullable = false)
    private Email email;

    /** 닉네임 **/
    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 100, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "detail_id",
            foreignKey = @ForeignKey(name = "FK_MEMBER_DETAIL"),
            nullable = false
    )
    private MemberDetail detail;

    public static Member register(MemberRegisterRequest createRequest, PasswordEncoder passwordEncoder) {
        var member = new Member();

        member.email = new Email(requireNonNull(createRequest.email()));
        member.nickname = requireNonNull(createRequest.nickname());
        member.passwordHash = requireNonNull(passwordEncoder.encode(createRequest.password()));

        member.status = MemberStatus.PENDING;

        member.detail = MemberDetail.create();

        return member;
    }

    public void activate() {
        state(status == MemberStatus.PENDING, "PENDING 상태가 아닙니다");

        this.status = MemberStatus.ACTIVE;
        this.detail.whenActivated();
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다");

        this.status = MemberStatus.DEACTIVATED;

        this.detail.whenDeactivated();
    }

    public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, passwordHash);
    }

    public void changeNickname(String nickname) {
        state(status == MemberStatus.ACTIVE, "'활성' 상태가 아닙니다.");
        isTrue(nickname.length() >= 5 && nickname.length() <= 20, "닉네임은 5 ~ 20 자 사이의 길이를 가져야 합니다");
        this.nickname = nickname;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        state(status == MemberStatus.ACTIVE, "'활성' 상태가 아닙니다.");
        isTrue(password.length() >= 8 && password.length() <= 100, "비밀번호는 8 ~ 100자 사이의 길이를 가져야 합니다");
        this.passwordHash = passwordEncoder.encode(password);
    }

    public void updateInfo(MemberInfoUpdateRequest updateRequest) {
        state(status == MemberStatus.ACTIVE, "'활성' 상태가 아닙니다.");
        this.detail.updateMainInfo(updateRequest);
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
