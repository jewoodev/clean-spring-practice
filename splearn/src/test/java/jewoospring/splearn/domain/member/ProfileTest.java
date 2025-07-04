package jewoospring.splearn.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {
    @Test
    void success() {
        new Profile("@jewoo15");
        new Profile("@saffe");
    }

    @Test
    void fail() {
        assertThatThrownBy(() -> new Profile("jewoo"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Profile("@jewoojewoojewoojewoojewoojewoojewoojewoojewoojewoojewoojewoojewoo"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}