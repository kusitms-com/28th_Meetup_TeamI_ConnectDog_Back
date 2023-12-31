package com.pawwithu.connectdog.domain.dogStatus.entity;

import com.pawwithu.connectdog.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class DogStatusImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String image;   // 근황 사진
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dogStatus_id", nullable = false)
    private DogStatus dogStatus;  // 근황 id

    @Builder
    public DogStatusImage(String image, DogStatus dogStatus) {
        this.image = image;
        this.dogStatus = dogStatus;
    }
}
