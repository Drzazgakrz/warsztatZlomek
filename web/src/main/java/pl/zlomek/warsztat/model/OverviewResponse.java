package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
public class OverviewResponse implements Serializable {
    private Date date;
    private CarResponseModel carResponseModel;

    public OverviewResponse(Overview overview) {
        this.date = Date.from(overview.getOverviewDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.carResponseModel = carResponseModel;
    }
}
