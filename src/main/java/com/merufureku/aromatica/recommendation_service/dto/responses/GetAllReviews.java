package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.time.LocalDate;
import java.util.List;

public record GetAllReviews(List<Interactions> interactions){

    public record Interactions(Integer userId, Long fragranceId, int rating, boolean hasCollection, LocalDate reviewDate){}
}
