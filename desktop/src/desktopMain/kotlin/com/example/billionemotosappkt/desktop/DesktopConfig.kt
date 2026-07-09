package com.example.billionemotosappkt.desktop

/**
 * Fonte unica de verdade para a configuracao do portal desktop.
 *
 * A URL base da API pode ser sobrescrita em runtime pela variavel de ambiente
 * `BILLIONE_API_BASE_URL` (ex.: apontar para producao ou para um tunel local),
 * caindo no valor padrao apenas quando a variavel nao estiver definida.
 *
 * Centralizar aqui evita que a URL fique duplicada em varios pontos do codigo e
 * fora de sincronia entre a tela publica e o portal administrativo.
 */
object DesktopConfig {
    /** Valor padrao usado quando `BILLIONE_API_BASE_URL` nao esta definida. */
    const val DEFAULT_API_BASE_URL: String = "https://engulf-blaming-scorpion.ngrok-free.dev"

    /** URL base efetiva da API, resolvida a partir do ambiente. */
    val apiBaseUrl: String
        get() = System.getenv("BILLIONE_API_BASE_URL")
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_API_BASE_URL
}
