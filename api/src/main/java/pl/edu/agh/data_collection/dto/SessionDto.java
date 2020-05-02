package pl.edu.agh.data_collection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.data_collection.config.validator.ProperEvent;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {

    @Valid
    private List<SessionElement> sessions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionElement{
        @Min(value = 0)
        private Integer x_cor;
        @Min(value = 0)
        private Integer y_cor;
        @ProperEvent
        private String event;
        private String time;
    }
}
