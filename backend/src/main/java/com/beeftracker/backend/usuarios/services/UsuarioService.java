package com.beeftracker.backend.usuarios.services;

import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.auth.repositories.UserRepository;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.fornecedores.models.Fornecedor;
import com.beeftracker.backend.email.EmailClient;
import com.beeftracker.backend.usuarios.models.Roles;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Service
public class UsuarioService {
    private final EmailClient emailClient;
    private final UserRepository repository;

    public UsuarioService(EmailClient emailClient, UserRepository repository) {
        this.emailClient = emailClient;
        this.repository = repository;
    }

    public void enviarEmail(String to, String token) throws ResendException {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from("CRM Frigorífico <suporte@beeftracker.xyz>")
                .to(to)
                .subject("Bem vindo ao Beef Tracker, seu cadastro foi realizado com sucesso!")
                .html("""
                    <html>
                      <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2>Bem-vindo ao Beef Tracker, %s!</h2>
                        <p>Seu cadastro foi realizado com sucesso.</p>
                        <p>Para ativar sua conta, clique no link abaixo:</p>
                        <a href="%s">Ativar minha conta</a>
                        <br><br>
                        <small>Se você não criou uma conta, ignore este e-mail.</small>
                      </body>
                    </html>
                """.formatted("enrique", "beeftracker.xyz/cadastro?token=" + token))
                .build();
        emailClient.enviarEmail(email);
    }

    public String gerarToken(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    }

    public void cadastrar(UserData user) throws ResendException {
        String token = gerarToken();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newSenha = passwordEncoder.encode(token);
        repository.salvar(user, token, newSenha);
        enviarEmail(user.email(), token);
    }

    public void finalizarCadastro(Long id, String senha, String token) throws ResourceNotFoundException {
        User user = repository.findByDataTokenPrimeiroAcesso(token);
        if(user == null || user.metadata().id() != id){
            throw new ResourceNotFoundException();
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newSenha = passwordEncoder.encode(senha);
        repository.finalizarCadastro(id, newSenha);
    }

    public Roles getRoles(Long userId){
        Roles roles = repository.findRolesByUser(userId);
        return roles;
    }

    public List<User> pesquisar(String chave, Boolean status) {
        List<User> usuarios = repository.pesquisar(chave, status);
        return usuarios;
    }
}


