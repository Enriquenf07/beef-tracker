package com.beeftracker.backend.email;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;

public class EmailClient {
    @Value("resend.api.key")
    private final String resendKey;
    Resend resend = new Resend("re_CBfhddaa_9KX87N22zqA42KmrWKN4x8k4");

    CreateEmailOptions email = CreateEmailOptions.builder()
            .from("CRM Frigorífico <pedidos@beeftracker.xyz>")
            .to("enriqueferreira956@gmail.com")
            .subject("Pedido #" + 1 + " confirmado")
            .html("""
                <h2>Olá, %s!</h2>
                <p>Seu pedido <strong>#%s</strong> foi confirmado.</p>
            """.formatted("enrique", 1))
            .build();

		resend.emails().send(email);
}
