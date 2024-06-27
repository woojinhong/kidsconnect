package com.example.kidsconnect.dao;

import com.example.kidsconnect.domain.*;
import com.example.kidsconnect.dto.MatchRequestDto;
import com.example.kidsconnect.dto.MatchResponseDto;
import com.example.kidsconnect.dto.TherapistInfoFilterDto;
import com.example.kidsconnect.dto.TopTherapistResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import com.querydsl.core.Tuple;

@Repository
@RequiredArgsConstructor
public class TherapistInfoRepositoryImpl implements TherapistInfoRepositoryCustom {

        private final JPAQueryFactory queryFactory;

        @Override
        public List<MatchResponseDto> findTherapistsByCriteria(MatchRequestDto matchRequestDto) {
                QTherapist qTherapist = QTherapist.therapist;
                QTherapistInfo qTherapistInfo = QTherapistInfo.therapistInfo;
                QSymptom qSymptom = QSymptom.symptom;
                QEnrol qEnrol = QEnrol.enrol;
                QCenter qCenter = QCenter.center;
                QTherapistInfoSymptom qTherapistInfoSymptom = QTherapistInfoSymptom.therapistInfoSymptom;

                // Using the method
   //             BooleanExpression experienceCondition = qTherapistInfo.totalExperience.ne("무경력");
                BooleanExpression experienceCondition = experienceEq(matchRequestDto.getIsExperience());
                BooleanExpression symptomCondition = symptomIn(matchRequestDto.getSymptoms());
                BooleanExpression genderCondition = genderEq(matchRequestDto.getGender());
                JPQLQuery<Long> subQuery = createSubQuery(genderCondition, experienceCondition, symptomCondition);
//                BooleanExpression genderCondition = genderEq(matchRequestDto.getGender());
//                BooleanExpression experienceCondition = experienceEq(matchRequestDto.isExperience());
//                BooleanExpression symptomCondition = symptomIn(matchRequestDto.getSymptoms());

                // 서브쿼리 정의
//                JPQLQuery<Long> subQuery = createSubQuery(genderCondition, experienceCondition, symptomCondition);

                // 메인 쿼리 정의
                List<Tuple> results = queryFactory
                        .select(
                                qTherapistInfo.id,
                                qTherapist.firstName,
                                qTherapistInfo.bio,
                                qTherapistInfo.imageFile,
                                qTherapistInfo.totalExperience,
                                qTherapist.freelancer.when(true).then("프리랜서").otherwise(qCenter.name),
                                qSymptom.name
                        )
                        .from(qTherapistInfo)
                        .join(qTherapistInfo.therapist, qTherapist)
                        .leftJoin(qTherapist.enrol, qEnrol)
                        .leftJoin(qEnrol.center, qCenter)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .where(qTherapistInfo.id.in(subQuery))
                        .fetch();

                Map<Long, MatchResponseDto> resultMap = new HashMap<>();

                for (Tuple row : results) {
                        Long therapistInfoId = row.get(qTherapistInfo.id);
                        String therapistName = row.get(qTherapist.firstName);
                        String bio = row.get(qTherapistInfo.bio);
                        byte[] imageFile = row.get(qTherapistInfo.imageFile);
                        String totalExperience = row.get(qTherapistInfo.totalExperience);
                        String centerName = row.get(qTherapist.freelancer.when(false).then("프리랜서").otherwise(qCenter.name));
                        String symptomName = row.get(qSymptom.name);

                        MatchResponseDto dto = resultMap.get(therapistInfoId);
                        if (dto == null) {
                                dto = new MatchResponseDto(
                                        therapistName,
                                        bio,
                                        imageFile,
                                        totalExperience,
                                        centerName,
                                        new ArrayList<>()
                                );
                                resultMap.put(therapistInfoId, dto);
                        }
                        if (symptomName != null && !dto.getSymptoms().contains(symptomName)) {
                                dto.getSymptoms().add(symptomName);
                        }
                }

                return resultMap.values().stream().collect(Collectors.toList());
        }

        protected JPQLQuery<Long> createSubQuery(BooleanExpression genderCondition, BooleanExpression experienceCondition, BooleanExpression symptomCondition) {
                QTherapist qTherapist = QTherapist.therapist;
                QTherapistInfo qTherapistInfo = QTherapistInfo.therapistInfo;
                QSymptom qSymptom = QSymptom.symptom;
                QEnrol qEnrol = QEnrol.enrol;
                QCenter qCenter = QCenter.center;
                QTherapistInfoSymptom qTherapistInfoSymptom = QTherapistInfoSymptom.therapistInfoSymptom;

                BooleanBuilder builder = new BooleanBuilder();

                if (genderCondition != null) {
                        builder.and(genderCondition);
                }

                if (experienceCondition != null) {
                        builder.and(experienceCondition);
                }

                if (symptomCondition != null) {
                        builder.and(symptomCondition);
                }
                return JPAExpressions
                        .select(qTherapistInfo.id)
                        .from(qTherapistInfo)
                        .join(qTherapistInfo.therapist, qTherapist)
                        .leftJoin(qTherapist.enrol, qEnrol)
                        .leftJoin(qEnrol.center, qCenter)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .where(
                                builder
                        );
        }

        private BooleanExpression genderEq(Character gender) {
                if (gender == null || gender.toString().isEmpty()) {
                        return null;
                }
                return QTherapist.therapist.gender.eq(gender);
        }

        private BooleanExpression experienceEq(boolean isExperience) {
                if (isExperience) {
                        return QTherapistInfo.therapistInfo.totalExperience.ne("무경력");
                }
                return QTherapistInfo.therapistInfo.totalExperience.eq("무경력");
        }

        private BooleanExpression symptomIn(List<String> symptoms) {
                if (symptoms != null && !symptoms.isEmpty()) {
                        return QSymptom.symptom.name.in(symptoms);
                }
                return null;
        }

        @Override
        public List<MatchResponseDto> findTherapistInfoByFilter(TherapistInfoFilterDto filterDto) {
                QTherapist qTherapist = QTherapist.therapist;
                QTherapistInfo qTherapistInfo = QTherapistInfo.therapistInfo;
                QSymptom qSymptom = QSymptom.symptom;
                QTherapistInfoSymptom qTherapistInfoSymptom = QTherapistInfoSymptom.therapistInfoSymptom;
                QTherapistReview qTherapistReview = QTherapistReview.therapistReview;
                QEnrol qEnrol = QEnrol.enrol;
                QCenter qCenter = QCenter.center;

                BooleanBuilder builder = new BooleanBuilder();

                if (filterDto.getAddress() != null && !filterDto.getAddress().isEmpty()) {
                        builder.and(qTherapist.address.containsIgnoreCase(filterDto.getAddress()));
                }
                if (filterDto.getGender() != null) {
                        builder.and(qTherapist.gender.eq(filterDto.getGender()));
                }
                if (filterDto.getIsExperience() != null) {
                        builder.and(filterDto.getIsExperience() ? qTherapistInfo.totalExperience.ne("무경력") : qTherapistInfo.totalExperience.eq("무경력"));
                }
                if (filterDto.getSymptoms() != null && !filterDto.getSymptoms().isEmpty()) {
                        builder.and(qSymptom.name.in(filterDto.getSymptoms()));
                }

                // Select all relevant therapistInfo IDs that match the filter criteria
                JPQLQuery<Long> subQuery = JPAExpressions
                        .select(qTherapistInfo.id)
                        .from(qTherapistInfo)
                        .join(qTherapistInfo.therapist, qTherapist)
                        .leftJoin(qTherapist.enrol, qEnrol)
                        .leftJoin(qEnrol.center, qCenter)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .where(builder);

                // Main query to fetch the details of therapistInfo along with related symptoms
                JPQLQuery<Tuple> query = queryFactory
                        .select(
                                qTherapistInfo.id,
                                qTherapist.firstName,
                                qTherapistInfo.bio,
                                qTherapistInfo.imageFile,
                                qTherapistInfo.totalExperience,
                                qTherapist.freelancer.when(true).then("프리랜서").otherwise(qCenter.name),
                                qSymptom.name,
                                qTherapistReview.rating.avg()
                        )
                        .from(qTherapistInfo)
                        .join(qTherapistInfo.therapist, qTherapist)
                        .leftJoin(qTherapist.enrol, qEnrol)
                        .leftJoin(qEnrol.center, qCenter)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .leftJoin(qTherapistInfo.therapistReview, qTherapistReview)
                        .where(qTherapistInfo.id.in(subQuery));

                if (filterDto.getSort() != null && !filterDto.getSort().isEmpty()) {
                        switch (filterDto.getSort()) {
                                case "date":
                                        query.orderBy(qTherapistInfo.inDate.desc());
                                        break;
                                case "rating":
                                        query.groupBy(
                                                        qTherapistInfo.id,
                                                        qTherapist.firstName,
                                                        qTherapistInfo.bio,
                                                        qTherapistInfo.imageFile,
                                                        qTherapistInfo.totalExperience,
                                                        qCenter.name,
                                                        qSymptom.name
                                                )
                                                .orderBy(qTherapistReview.rating.avg().desc(), qTherapistInfo.inDate.asc());
                                        break;
                                // Other sorting options can be added here
                        }
                }

                List<Tuple> results = query.fetch();

                Map<Long, MatchResponseDto> resultMap = new HashMap<>();

                for (Tuple row : results) {
                        Long therapistInfoId = row.get(qTherapistInfo.id);
                        String therapistName = row.get(qTherapist.firstName);
                        String bio = row.get(qTherapistInfo.bio);
                        byte[] imageFile = row.get(qTherapistInfo.imageFile);
                        String totalExperience = row.get(qTherapistInfo.totalExperience);
                        String centerName = row.get(qTherapist.freelancer.when(true).then("프리랜서").otherwise(qCenter.name));
                        String symptomName = row.get(qSymptom.name);

                        MatchResponseDto dto = resultMap.get(therapistInfoId);
                        if (dto == null) {
                                dto = MatchResponseDto.builder()
                                        .therapistName(therapistName)
                                        .bio(bio)
                                        .imageFile(imageFile)
                                        .totalExperience(totalExperience)
                                        .centerName(centerName)
                                        .symptoms(new ArrayList<>())
                                        .build();
                                resultMap.put(therapistInfoId, dto);
                        }
                        if (symptomName != null && !dto.getSymptoms().contains(symptomName)) {
                                dto.getSymptoms().add(symptomName);
                        }
                }

                // 추가 로직: therapistInfoId 기준으로 증상들을 그룹화하여 전체 증상을 반환
                Set<Long> therapistInfoIds = resultMap.keySet();
                List<Tuple> allSymptoms = queryFactory
                        .select(qTherapistInfo.id, qSymptom.name)
                        .from(qTherapistInfo)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .where(qTherapistInfo.id.in(therapistInfoIds))
                        .fetch();

                for (Tuple symptomRow : allSymptoms) {
                        Long therapistInfoId = symptomRow.get(qTherapistInfo.id);
                        String symptomName = symptomRow.get(qSymptom.name);

                        MatchResponseDto dto = resultMap.get(therapistInfoId);
                        if (dto != null && symptomName != null && !dto.getSymptoms().contains(symptomName)) {
                                dto.getSymptoms().add(symptomName);
                        }
                }

                return new ArrayList<>(resultMap.values());
        }
        @Override
        public List<TopTherapistResponseDto> findTopTherapistsOfMonth() {
                QTherapist qTherapist = QTherapist.therapist;
                QTherapistInfo qTherapistInfo = QTherapistInfo.therapistInfo;
                QSymptom qSymptom = QSymptom.symptom;
                QTherapistInfoSymptom qTherapistInfoSymptom = QTherapistInfoSymptom.therapistInfoSymptom;
                QTherapistReview qTherapistReview = QTherapistReview.therapistReview;
                QEnrol qEnrol = QEnrol.enrol;
                QCenter qCenter = QCenter.center;

                JPQLQuery<Tuple> query = queryFactory
                        .select(
                                qTherapistInfo.id,
                                qTherapist.firstName,
                                qTherapistInfo.bio,
                                qTherapistInfo.imageFile,
                                qTherapistInfo.totalExperience,
                                qTherapist.freelancer, // 추가된 필드
                                qCenter.name,
                                qSymptom.name,
                                qTherapistReview.rating.avg().coalesce(0.0).as("averageRating")
                        )
                        .from(qTherapistInfo)
                        .join(qTherapistInfo.therapist, qTherapist)
                        .leftJoin(qTherapist.enrol, qEnrol)
                        .leftJoin(qEnrol.center, qCenter)
                        .leftJoin(qTherapistInfo.therapistInfoSymptom, qTherapistInfoSymptom)
                        .leftJoin(qTherapistInfoSymptom.symptom, qSymptom)
                        .leftJoin(qTherapistInfo.therapistReview, qTherapistReview)
                        .groupBy(
                                qTherapistInfo.id,
                                qTherapist.firstName,
                                qTherapistInfo.bio,
                                qTherapistInfo.imageFile,
                                qTherapistInfo.totalExperience,
                                qTherapist.freelancer, // 추가된 필드
                                qCenter.name,
                                qSymptom.name
                        )
                        .orderBy(qTherapistReview.rating.avg().desc(), qTherapistInfo.inDate.desc())
                        .limit(4);

                List<Tuple> results = query.fetch();

                Map<Long, TopTherapistResponseDto> resultMap = new HashMap<>();

                for (Tuple row : results) {
                        Long therapistInfoId = row.get(qTherapistInfo.id);
                        String therapistName = row.get(qTherapist.firstName);
                        String bio = row.get(qTherapistInfo.bio);
                        byte[] imageFile = row.get(qTherapistInfo.imageFile);
                        String totalExperience = row.get(qTherapistInfo.totalExperience);
                        Boolean freelancer = row.get(qTherapist.freelancer);
                        String centerName = freelancer != null && freelancer ? "프리랜서" : row.get(qCenter.name);
                        String symptomName = row.get(qSymptom.name);
                        Double avgRating = row.get(qTherapistReview.rating.avg());

                        TopTherapistResponseDto dto = resultMap.get(therapistInfoId);
                        if (dto == null) {
                                dto = TopTherapistResponseDto.builder()
                                        .therapistName(therapistName)
                                        .bio(bio)
                                        .imageFile(imageFile)
                                        .totalExperience(totalExperience)
                                        .centerName(centerName)
                                        .symptoms(new ArrayList<>())
                                        .avgRating(avgRating != null ? avgRating : 0.0) // Handle null avgRating
                                        .build();
                                resultMap.put(therapistInfoId, dto);
                        }
                        if (symptomName != null && !dto.getSymptoms().contains(symptomName)) {
                                dto.getSymptoms().add(symptomName);
                        }
                }

                return new ArrayList<>(resultMap.values());
        }
}