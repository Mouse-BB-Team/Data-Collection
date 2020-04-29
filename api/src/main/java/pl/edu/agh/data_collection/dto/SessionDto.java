package pl.edu.agh.data_collection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {

    private List<SessionElement> sessions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionElement{
        private Long user_id;
        private Integer x_cor;
        private Integer y_cor;
        private String event;
        private String time;
    }
}
