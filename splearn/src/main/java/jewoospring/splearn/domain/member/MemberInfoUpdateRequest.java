package jewoospring.splearn.domain.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberInfoUpdateRequest(
        @NotNull
        @Size(min = 2, max = 20)
        String profileAddress,

        @NotNull
        @Size(min = 5, max = 50)
        String introduction
) {
}
