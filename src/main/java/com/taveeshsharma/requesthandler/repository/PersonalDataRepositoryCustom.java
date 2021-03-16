package com.taveeshsharma.requesthandler.repository;

import java.util.Date;

public interface PersonalDataRepositoryCustom {
    Date findLastSummaryCheckIn(String deviceId);
}
