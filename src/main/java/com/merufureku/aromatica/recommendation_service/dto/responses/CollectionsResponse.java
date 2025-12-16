package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.util.List;

public record CollectionsResponse(List<FragranceDetails> fragrances) {

    public record FragranceDetails(Integer userId, Long fragranceId) { }

}
