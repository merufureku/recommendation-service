package com.merufureku.aromatica.recommendation_service.dto.params;

import java.util.Set;

public record ExcludeFragranceBatchParam(Set<Long> excludedFragranceIds) {}
