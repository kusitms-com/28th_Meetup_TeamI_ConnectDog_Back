package com.pawwithu.connectdog.domain.post.dto.request;

import com.pawwithu.connectdog.domain.dog.entity.DogSize;
import com.pawwithu.connectdog.domain.post.entity.PostStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public record PostSearchRequest(@RequestParam(value = "postStatus", required = false) PostStatus postStatus,
                                String departureLoc,
                                String arrivalLoc,
                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                LocalDate startDate,
                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                LocalDate endDate,
                                @RequestParam(value = "dogSize", required = false) DogSize dogSize,
                                Boolean isKennel,
                                String intermediaryName) {
    
}