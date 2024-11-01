package com.example.demo.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OverviewResponse {
    private int logicalQuestionsCount;
    private int technicalQuestionsCount;
    private int programmingQuestionsCount;
    private int examCount;
    private int studentCount;
    private int collegesCount;
}
