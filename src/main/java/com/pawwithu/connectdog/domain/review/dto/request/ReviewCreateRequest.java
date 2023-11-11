package com.pawwithu.connectdog.domain.review.dto.request;

import com.pawwithu.connectdog.domain.post.entity.Post;
import com.pawwithu.connectdog.domain.review.entity.Review;
import com.pawwithu.connectdog.domain.volunteer.entity.Volunteer;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(@NotBlank(message = "후기 내용은 필수 입력 값입니다.")
                                  String content) {

    public static Review reviewToEntity(ReviewCreateRequest request, Volunteer volunteer, Post post) {
        return Review.builder()
                .content(request.content())
                .volunteer(volunteer)
                .post(post)
                .build();
    }

}
