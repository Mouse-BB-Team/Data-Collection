package pl.edu.agh.data_collection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.data_collection.config.ContextPath;
import pl.edu.agh.data_collection.config.ProfileType;
import pl.edu.agh.data_collection.config.UserRole;
import pl.edu.agh.data_collection.dto.UserDto;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;
import pl.edu.agh.data_collection.persistence.repository.UserRepository;

import java.util.Optional;

import static pl.edu.agh.data_collection.exception.BadCredentialsException.ExceptionMessage.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ActiveProfiles(value = ProfileType.TEST_PROFILE)
class UserControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @MockBean
    private PasswordEncoder encoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void validateLoginMinLengthTest() throws Exception {
        UserDto request = new UserDto("admin", "password");

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validateLoginMaxLengthTest() throws Exception {
        UserDto request = new UserDto(generateToLongString(), "password");

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validatePasswordMinLengthTest() throws Exception {
        UserDto request = new UserDto("administrator", "pass");

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validatePasswordMaxLengthTest() throws Exception {
        UserDto request = new UserDto("administrator", generateToLongString());

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void checkIfUserAlreadyExistsTest() throws Exception {
        UserDto request = new UserDto("administrator", "password");
        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = USER_ALREADY_EXISTS.toString();

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void saveProperlyUserToDatabaseTest() throws Exception {
        String login = "administrator";
        String password = "password";

        UserDto request = new UserDto(login, password);
        byte[] content = MAPPER.writeValueAsBytes(request);

        UserEntity expectedSavedUser = new UserEntity();
        expectedSavedUser.setLogin(login);
        expectedSavedUser.setPassword(encoder.encode(password));
        expectedSavedUser.setAuthority(UserRole.USER);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());

        verify(userRepository).save(userCaptor.capture());

        assertEquals(expectedSavedUser, userCaptor.getValue());
    }

    private String generateToLongString(){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            stringBuilder.append('p');
        }

        return stringBuilder.toString();
    }
}