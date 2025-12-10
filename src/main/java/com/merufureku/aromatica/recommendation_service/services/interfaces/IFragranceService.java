package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceDetailedListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;

public interface IFragranceService {

    BaseResponse<FragranceDetailedListResponse> getPerfumes(GetFragranceBatchParam param, int version, String correlationId);

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(GetFragranceBatchParam param, int version, String correlationId);

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(ExcludeFragranceBatchParam param, int version, String correlationId);
}
