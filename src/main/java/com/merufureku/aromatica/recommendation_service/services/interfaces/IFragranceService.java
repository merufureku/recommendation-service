package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.params.FragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceResponse;

public interface IFragranceService {

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(FragranceBatchNotesParam param, int version, String correlationId);

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(ExcludeFragranceBatchNotesParam param, int version, String correlationId);

    BaseResponse<FragranceResponse> getPerfumeById(Long fragranceId, int version, String correlationId);
}
