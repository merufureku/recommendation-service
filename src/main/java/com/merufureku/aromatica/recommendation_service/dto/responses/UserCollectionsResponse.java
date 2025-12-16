package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.util.List;

public record UserCollectionsResponse(Integer userId, List<FragranceDetails> fragrances) {

    public record FragranceDetails(Long fragranceId, String name, String brand) {}

}
