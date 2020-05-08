package pl.edu.agh.data_collection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final String PROPER_LOGIN = "administrator";
    private final String PROPER_PASSWORD = "password";
    private final String TOO_SHORT_LOGIN = "admin";
    private final String TOO_SHORT_PASSWORD = "pass";
    private final String TOO_LONG_LOGIN = generateToLongString();
    private final String TOO_LONG_PASSWORD = generateToLongString();

    @SpyBean
    private BCryptPasswordEncoder encoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void validateLoginMinLengthCreateUserEndpointTest() throws Exception {
        UserDto request = new UserDto(TOO_SHORT_LOGIN, PROPER_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validateLoginMaxLengthCreateUserEndpointTest() throws Exception {
        UserDto request = new UserDto(TOO_LONG_LOGIN, PROPER_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validatePasswordMinLengthCreateUserEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, TOO_SHORT_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validatePasswordMaxLengthCreateUserEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, TOO_LONG_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void checkIfUserAlreadyExistsCreateUserEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, PROPER_PASSWORD);
        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = USER_ALREADY_EXISTS.toString();

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void saveProperlyUserToDatabaseCreateUserEndpointTest() throws Exception {
        String login = "administrator";
        String password = "password";
        String role = UserRole.USER;

        UserDto request = new UserDto(login, password);
        byte[] content = MAPPER.writeValueAsBytes(request);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());

        verify(userRepository).save(userCaptor.capture());

        assertEquals(login, userCaptor.getValue().getLogin());
        assertTrue(encoder.matches(password, userCaptor.getValue().getPassword()));
        assertEquals(role, userCaptor.getValue().getAuthority());
    }

    @Test
    void validateLoginMinLengthCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(TOO_SHORT_LOGIN, PROPER_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validateLoginMaxLengthCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(TOO_LONG_LOGIN, PROPER_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void validatePasswordMinLengthCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, TOO_SHORT_PASSWORD);

        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void checkIfUserLoginExistsCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, PROPER_PASSWORD);
        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void checkIfUserPasswordIsProperCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, PROPER_PASSWORD);
        byte[] content = MAPPER.writeValueAsBytes(request);
        String response = BAD_LOGIN_OR_PASSWORD.toString();

        UserEntity userFromRepository = new UserEntity(1L, PROPER_LOGIN, encoder.encode(PROPER_PASSWORD), UserRole.USER);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(userFromRepository));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest()).andExpect(content().string(response));
    }

    @Test
    void checkProperlyUserCredentialsCheckUserCredentialsEndpointTest() throws Exception {
        UserDto request = new UserDto(PROPER_LOGIN, PROPER_PASSWORD);
        byte[] content = MAPPER.writeValueAsBytes(request);

        UserEntity userFromRepository = new UserEntity(1L, PROPER_LOGIN, encoder.encode(PROPER_PASSWORD), UserRole.USER);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(userFromRepository));

        mockMvc.perform(post(ContextPath.USER_MAIN_PATH + ContextPath.USER_CHECK_CREDENTIALS_PATH).contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk());
    }

    private String generateToLongString(){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            stringBuilder.append('p');
        }

        return stringBuilder.toString();
    }
}