package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.util.List;

public record FragranceNoteListResponse(List<FragranceNoteList> fragranceNoteLists) {

    public record FragranceNoteList(Long fragranceId, List<NoteResponse> noteResponseList) {}

}
