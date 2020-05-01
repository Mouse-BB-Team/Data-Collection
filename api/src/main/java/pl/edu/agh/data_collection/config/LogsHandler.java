package pl.edu.agh.data_collection.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Aspect
@Configuration
@Profile(value = {ProfileType.ONLY_ADMIN_CREATES_USERS, ProfileType.USERS_CREATE_THEMSELVES})
class LogsHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut(value = "execution(org.springframework.http.ResponseEntity *.*(..)) && args(..,request) && @annotation(pl.edu.agh.data_collection.config.WarningLog)")
    public void exceptionWarningPointcut(WebRequest request) {
        // Do nothing because of it's pointcut.
    }

    @Around(value = "exceptionWarningPointcut(request)", argNames = "joinPoint, request")
    public Object doExceptionWarningLogs(ProceedingJoinPoint joinPoint, WebRequest request) throws Throwable {
        ResponseEntity<Object> returnedValue = (ResponseEntity<Object>) joinPoint.proceed();

        String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
        String method = ((ServletWebRequest) request).getRequest().getMethod();

        Object body = returnedValue.getBody();

        log.warn("{}: {}  ({}) ---> {}", method, uri, request.getRemoteUser(), body);
        return joinPoint.proceed();
    }
}
