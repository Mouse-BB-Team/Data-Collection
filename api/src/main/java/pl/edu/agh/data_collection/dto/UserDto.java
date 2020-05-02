package pl.edu.agh.data_collection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.agh.data_collection.config.StartupConfig;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Size(min = StartupConfig.MIN_LOGIN_LENGTH, max = StartupConfig.MAX_LOGIN_LENGTH)
    private String login;
    @Size(min = StartupConfig.MIN_PASSWORD_LENGTH, max = StartupConfig.MAX_PASSWORD_LENGTH)
    private String password;
}
