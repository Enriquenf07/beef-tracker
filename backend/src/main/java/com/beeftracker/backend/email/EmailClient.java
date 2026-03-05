package com.beeftracker.backend.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailClient {

    private final String resendKey;

    public EmailClient(@Value("resend.api.key") String resendKey) {
        this.resendKey = resendKey;
    }
    public void enviarEmail(CreateEmailOptions email) throws ResendException {
        Resend resend = new Resend("re_EArg7DXU_5AZneDAuC5Eb3QwSnNDxn2Ey");
        resend.emails().send(email);
    }


}
