package cloud.tianai.captcha.spring.plugins.secondary;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.autoconfiguration.SecondaryVerificationProperties;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SecondaryVerificationApplicationTest {
    ImageCaptchaApplication target;
    SecondaryVerificationApplication application;

    @BeforeEach
    void setUp() {
        target = mock(ImageCaptchaApplication.class);
        doReturn(new LocalCacheStore()).when(target).getCacheStore();
        SecondaryVerificationProperties properties = new SecondaryVerificationProperties();
        properties.setEnabled(true);
        properties.setExpire(20000L);
        application = new SecondaryVerificationApplication(target, properties);
    }

    @Test
    void secondaryVerificationByMatchParam() {
        String id = "SLIDER_8e672c73dfff40ffb8d812dbc9047bb0";
        ImageCaptchaTrack track = new ImageCaptchaTrack();
        MatchParam matchParam = new MatchParam();
        matchParam.setTrack(track);
        ApiResponse<Object> matched = ApiResponse.ofSuccess(null);
        matched.setData(Collections.singletonMap("id", id));
        doReturn(matched).when(target).matching(eq(id), eq(matchParam));

        ApiResponse<?> matching = application.matching(id, matchParam);

        verify(target).matching(id, matchParam);

        assertTrue(matching.isSuccess());
        assertEquals(id, ((java.util.Map<?, ?>) matching.getData()).get("id"));

        boolean b = application.secondaryVerification(id);
        assertTrue(b);
        b = application.secondaryVerification(id);
        assertFalse(b);
    }
}
