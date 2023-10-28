package com.pawwithu.connectdog.domain.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email; // 이메일
    private String password; // 비밀번호
    @Column(length = 15, nullable = false)
    private String nickname; // 닉네임
    @Column(length = 10)
    private String name; // 이름
    private String phone; // 이동봉사자 휴대폰 번호
    @Enumerated(EnumType.STRING)
    private VolunteerRole role; // 권한
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER
    private String socialId; // 로그인한 소셜 타입 식별자 값 (일반 로그인의 경우 null)
    private Boolean isOptionAgr; // 선택 이용약관 체크 여부

    @Builder
    public Volunteer(String email, String password, String nickname, VolunteerRole role, Boolean isOptionAgr) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isOptionAgr = isOptionAgr;
    }

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}