package com.pawwithu.connectdog.domain.application.service;

import com.pawwithu.connectdog.domain.application.dto.request.VolunteerApplyRequest;
import com.pawwithu.connectdog.domain.application.entity.Application;
import com.pawwithu.connectdog.domain.application.repository.ApplicationRepository;
import com.pawwithu.connectdog.domain.intermediary.entity.Intermediary;
import com.pawwithu.connectdog.domain.post.entity.Post;
import com.pawwithu.connectdog.domain.post.entity.PostStatus;
import com.pawwithu.connectdog.domain.post.repository.PostRepository;
import com.pawwithu.connectdog.domain.volunteer.entity.Volunteer;
import com.pawwithu.connectdog.domain.volunteer.repository.VolunteerRepository;
import com.pawwithu.connectdog.error.ErrorCode;
import com.pawwithu.connectdog.error.exception.custom.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pawwithu.connectdog.error.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationService {

    private final VolunteerRepository volunteerRepository;
    private final PostRepository postRepository;
    private final ApplicationRepository applicationRepository;

    public void volunteerApply(String email, Long postId, VolunteerApplyRequest request) {
        // 이동봉사자
        Volunteer volunteer = volunteerRepository.findByEmail(email).orElseThrow(() -> new BadRequestException(VOLUNTEER_NOT_FOUND));
        // 공고
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException(POST_NOT_FOUND));
        // 이동봉사 중개
        Intermediary intermediary = post.getIntermediary();

        // 해당 공고에 대한 신청이 이미 존재할 경우
        if (applicationRepository.existsByPostId(postId)) {
            throw new BadRequestException(ALREADY_EXIST_APPLICATION);
        }

        // 공고 신청 저장
        Application application = request.toEntity(post, intermediary, volunteer);
        applicationRepository.save(application);

        // 공고 상태 승인 대기 중으로 변경
        post.updateStatus(PostStatus.WAITING);
    }
}