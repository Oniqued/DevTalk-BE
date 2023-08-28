package com.devtalk.consultation.consultationservice.consultation.application;

import com.devtalk.consultation.consultationservice.consultation.application.port.in.dto.ConsultationRes;
import com.devtalk.consultation.consultationservice.consultation.application.port.out.repository.ConsultationQueryableRepo;
import com.devtalk.consultation.consultationservice.consultation.domain.consultation.Consultation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.devtalk.consultation.consultationservice.consultation.application.port.in.dto.ConsultationRes.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchConsultationService {
    private final ConsultationQueryableRepo consultationQueryableRepo;

    public List<ConsultationSearchRes> searchConsultationBy(Long consulterId) {
        List<Consultation> consultationList = consultationQueryableRepo.findAllByConsulterId(consulterId);

        return consultationList.stream().map(consultation -> ConsultationSearchRes.of(consultation)).toList();
    }
}
