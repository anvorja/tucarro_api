package com.anborja.tucarro.domain.spi;

public interface IPasswordEncoderPort {

    /**
     * Codifica una contraseña en texto plano
     *
     * @param rawPassword la contraseña en texto plano
     * @return la contraseña codificada
     */
    String encode(String rawPassword);

    /**
     * Verifica si una contraseña en texto plano coincide con una contraseña codificada
     *
     * @param rawPassword la contraseña en texto plano
     * @param encodedPassword la contraseña codificada
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    boolean matches(String rawPassword, String encodedPassword);

    /**
     * Verifica si una contraseña codificada necesita ser recodificada
     * Útil cuando se cambian algoritmos de codificación
     *
     * @param encodedPassword la contraseña codificada
     * @return true si necesita ser recodificada, false en caso contrario
     */
    boolean upgradeEncoding(String encodedPassword);
}