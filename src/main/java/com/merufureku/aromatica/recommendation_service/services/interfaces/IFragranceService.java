package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.FragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;

public interface IFragranceService {

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(FragranceBatchNotesParam param, int version, String correlationId);

    BaseResponse<FragranceNoteListResponse> getPerfumeNotes(int version, String correlationId);
}
