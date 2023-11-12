package com.pawwithu.connectdog.domain.post.repository.impl;

import com.pawwithu.connectdog.domain.bookmark.repository.BookmarkRepository;
import com.pawwithu.connectdog.domain.dog.entity.DogSize;
import com.pawwithu.connectdog.domain.post.dto.request.PostSearchRequest;
import com.pawwithu.connectdog.domain.post.dto.response.PostGetHomeResponse;
import com.pawwithu.connectdog.domain.post.dto.response.PostGetOneResponse;
import com.pawwithu.connectdog.domain.post.dto.response.PostSearchResponse;
import com.pawwithu.connectdog.domain.post.entity.PostStatus;
import com.pawwithu.connectdog.domain.post.repository.CustomPostRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.pawwithu.connectdog.domain.dog.entity.QDog.dog;
import static com.pawwithu.connectdog.domain.intermediary.entity.QIntermediary.intermediary;
import static com.pawwithu.connectdog.domain.post.entity.QPost.post;
import static com.pawwithu.connectdog.domain.post.entity.QPostImage.postImage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<PostGetHomeResponse> getHomePosts() {
        return queryFactory
                        .select(Projections.constructor(PostGetHomeResponse.class,
                                postImage.image, post.departureLoc, post.arrivalLoc, post.startDate, post.endDate,
                                intermediary.name, post.isKennel))
                        .from(post)
                        .join(post.intermediary, intermediary)
                        .join(post.mainImage, postImage)
                        .orderBy(post.createdDate.desc())
                        .limit(5)
                        .fetch();
    }

    @Override
    public List<PostSearchResponse> searchPosts(PostSearchRequest request, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(PostSearchResponse.class,
                        postImage.image, post.departureLoc, post.arrivalLoc, post.startDate, post.endDate,
                        intermediary.name, post.isKennel))
                .from(post)
                .join(post.intermediary, intermediary)
                .join(post.mainImage, postImage)
                .where(allFilterSearch(request, pageable))
                .orderBy(post.endDate.asc(), post.createdDate.desc())
                .offset(pageable.getOffset())   // 페이지 번호
                .limit(pageable.getPageSize())  // 페이지 사이즈
                .fetch();
    }

    @Override
    public List<String> getOnePostImages(Long postId) {
        return queryFactory
                .select(postImage.image)
                .from(postImage)
                .join(postImage.post, post)
                .where(postImage.post.id.eq(postId)
                        .and(post.mainImage.id.ne(postImage.id)))
                .fetch();
    }

    @Override
    public PostGetOneResponse getOnePost(Long volunteerId, Long postId) {
        return queryFactory
                .select(Projections.constructor(PostGetOneResponse.class,
                        postImage.image, post.status, post.departureLoc, post.arrivalLoc,
                        post.startDate, post.endDate, post.pickUpTime, post.isKennel, post.content,
                        dog.name, dog.size, dog.gender, dog.weight, dog.specifics,
                        intermediary.id, intermediary.profileImage, intermediary.name))
                .from(post)
                .join(post.intermediary, intermediary)
                .join(post.mainImage, postImage)
                .join(post.dog, dog)
                .where(post.id.eq(postId))
                .fetchOne();
    }

    // 모든 필터 검색
    private BooleanExpression allFilterSearch(PostSearchRequest request, Pageable pageable) {
        return postStatusEq(request.postStatus())
                .and(departureLocContains(request.departureLoc()))
                .and(arrivalLocContains(request.arrivalLoc()))
                .and(dateSearch(request.startDate(), request.endDate()))
                .and(dogSizeEq(request.dogSize()))
                .and(isKennelEq(request.isKennel()))
                .and(intermediaryNameContains(request.intermediaryName()));
    }

    // 공고 상태 필터
    private BooleanExpression postStatusEq(PostStatus postStatus) {
        return postStatus == null ? null : post.status.eq(postStatus);
    }

    // 출발 지역 필터
    private BooleanExpression departureLocContains(String departureLoc) {
        return StringUtils.hasText(departureLoc) ? post.departureLoc.contains(departureLoc) : null;
    }

    // 도착 지역 필터
    private BooleanExpression arrivalLocContains(String arrivalLoc) {
        return StringUtils.hasText(arrivalLoc) ? post.arrivalLoc.contains(arrivalLoc) : null;
    }

    // 일정 필터
    private BooleanExpression dateSearch(LocalDate userStartDate, LocalDate userEndDate) {
        if (userStartDate == null || userEndDate == null) return null;
        return post.startDate.loe(userEndDate).and(post.endDate.goe(userStartDate));
    }

    // 상세 정보 - 강아지 사이즈 필터
    private BooleanExpression dogSizeEq(DogSize dogSize) {
        if (dogSize == null) return null;
        return dog.size.eq(dogSize);
    }

    // 상세 정보 - 켄넬 제공 여부 필터
    private BooleanExpression isKennelEq(Boolean isKennel) {
        if (isKennel == null) return null;
        return post.isKennel.eq(isKennel);
    }

    // 상세 정보 - 이동봉사 중개명 필터
    private BooleanExpression intermediaryNameContains(String intermediaryName) {
        return StringUtils.hasText(intermediaryName) ? post.intermediary.name.contains(intermediaryName) : null;
    }

}
