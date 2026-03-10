package br.com.uatz.service;

public interface PasswordService {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}

