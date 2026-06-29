# Shared API Client

Base multiplataforma para consumir a API do Billione Motos com Ktor e kotlinx.serialization.

## Estrutura

- `ApiConfig` centraliza `baseUrl`, token bearer e headers globais.
- `BillioneMotosApi` expõe grupos de endpoints tipados.
- `ApiModels.kt` concentra enums, requests e responses.
- `HttpClientFactory` cria o cliente Ktor por plataforma.

## Uso

```kotlin
val api = BillioneMotosApi(
    ApiConfig(
        baseUrl = "http://localhost:3000",
        accessTokenProvider = { tokenStore.accessToken },
    )
)

val session = api.auth.login(
    CreateLoginRequest(
        email = "joao@exemplo.com",
        password = "SenhaForte123",
    )
)
```

## Sessao Android

No app Android, a composicao recomendada e:

- `TokenSessionStore` para persistir `access_token` e `refresh_token` no `DataStore`
- `BillioneMotosSessionManager` para `login`, `refresh`, `logout` e `logoutAll`
- `BillioneMotosAppContainer` para compartilhar uma unica instancia do client e do cache em memoria

Antes de usar a API autenticada, chame `sessionManager.initialize()` uma vez para hidratar o cache a partir do `DataStore`.

## Contrato observado

- Tokens de login usam `access_token` e `refresh_token`.
- Datas e valores monetarios sao tratados como `String` no modelo, mantendo a tipagem e evitando perda de precisao.
- As queries seguem os DTOs do backend documentados em `docs/api-contract-matrix.md`.
