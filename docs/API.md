# Endpoints da API

A API responde sempre em JSON. Datas usam o formato `AAAA-MM-DD`.

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/cientistas` | Público | Lista resumida de todas as cientistas (cards do Catálogo) |
| `GET` | `/cientistas/{id}` | Público | Dados completos de uma cientista (página de Perfil) |
| `GET` | `/areas` | Público | Áreas de atuação com o total de cientistas (filtros e página inicial) |
| `POST` | `/sugestoes` | Público | Grava a sugestão de uma nova cientista enviada por um visitante |
| `POST` | `/auth/login` | Público | Entra na área da equipe |
| `GET` | `/auth/eu` | Equipe | Mostra quem está logado |
| `POST` | `/auth/logout` | Equipe | Sai da área da equipe |
| `GET` | `/admin/sugestoes` | Equipe | Lista as sugestões, com filtro opcional por status |
| `PATCH` | `/admin/sugestoes/{id}` | Equipe | Aprova ou recusa uma sugestão |

## Formato dos erros

Todos os erros usam o mesmo formato:

```json
{
  "status": 400,
  "mensagem": "Confira os campos destacados.",
  "campos": { "emailSugestor": "Informe um e-mail válido." }
}
```

`campos` traz a mensagem de cada campo com problema, para o site mostrar ao lado do campo. Nos outros erros fica vazio (`{}`).

| Situação | Status | `mensagem` |
|---|---|---|
| Campo inválido no formulário | `400` | `Confira os campos destacados.` (detalhes em `campos`) |
| Corpo ausente ou JSON inválido | `400` | `Não foi possível ler os dados enviados.` |
| Valor inválido no endereço (ex.: `?status=QUALQUER`) | `400` | `Valor inválido em status.` |
| E-mail ou senha errados no login | `401` | `E-mail ou senha incorretos.` |
| Rota da equipe sem login | `401` | `Entre com seu e-mail e senha para continuar.` |
| Ação que altera dados sem o token CSRF | `403` | `Não foi possível confirmar esta ação. Recarregue a página e tente de novo.` |
| Item que não existe | `404` | Ex.: `Cientista não encontrada.`, `Sugestão não encontrada.` |

---

# Parte pública

## GET /cientistas

Retorna as cientistas em ordem alfabética, no formato usado pelos cards do Catálogo.

- **Sucesso:** `200 OK`
- **Sem cientistas cadastradas:** `200 OK` com lista vazia `[]`

```json
[
  {
    "id": 1,
    "nome": "Marie Curie",
    "resumoBiografia": "Física e química nascida em Varsóvia e radicada na França. Pioneira no estudo da radioatividade — termo que ela mesma cunhou —, foi a primeira…",
    "instituicao": "Universidade de Paris",
    "dataNascimento": "1867-11-07",
    "dataFalecimento": "1934-07-04",
    "nacionalidade": "Polonesa e francesa",
    "areasAtuacao": [
      { "id": 1, "nome": "Física" },
      { "id": 2, "nome": "Química" }
    ]
  }
]
```

| Campo | Origem no banco |
|---|---|
| `id` | `cientista.id_cientista` |
| `nome` | `cientista.nome` |
| `resumoBiografia` | Início de `cientista.biografia`: até 160 caracteres, sem cortar palavra, com "…" quando cortado |
| `instituicao` | `cientista.instituicao_principal` |
| `dataNascimento`, `dataFalecimento` | `cientista.data_nascimento` e `cientista.data_falescimento` (`null` se viva). A coluna mantém a grafia do modelo entregue; no JSON o nome é `dataFalecimento`. |
| `nacionalidade` | `cientista.nacionalidade` |
| `areasAtuacao` | Áreas da cientista (`cientista_tem_area_atuacao` + `area_atuacao`), em ordem alfabética. Uma cientista pode ter várias áreas. |

## GET /cientistas/{id}

Retorna os dados completos de uma cientista, com contribuições e prêmios em ordem cronológica.

- **Sucesso:** `200 OK`
- **Id inexistente:** `404 Not Found` com `{"status": 404, "mensagem": "Cientista não encontrada.", "campos": {}}`

```json
{
  "id": 1,
  "nome": "Marie Curie",
  "biografia": "Física e química nascida em Varsóvia…",
  "instituicao": "Universidade de Paris",
  "dataNascimento": "1867-11-07",
  "dataFalecimento": "1934-07-04",
  "nacionalidade": "Polonesa e francesa",
  "areasAtuacao": [{ "id": 1, "nome": "Física" }, { "id": 2, "nome": "Química" }],
  "contribuicoes": [
    { "id": 1, "ano": 1898, "titulo": "Descoberta do polônio e do rádio", "descricao": "…", "linkReferencia": "https://…" }
  ],
  "premios": [
    { "id": 1, "nome": "Prêmio Nobel de Física", "concedente": "Academia Real das Ciências da Suécia", "ano": 1903 }
  ]
}
```

| Campo | Origem no banco |
|---|---|
| `contribuicoes` | `cientista_tem_contribuicao` + `contribuicao`. Uma contribuição pode pertencer a mais de uma cientista. |
| `premios` | `cientista_tem_premio` + `premio`. O `id` é o do prêmio (`premio.id_premio`) e `concedente` vem de `premio.instituicao`. |

O prêmio mostra só o **ano** em que foi recebido (coluna `cientista_tem_premio.recebido_em`).

## GET /areas

Retorna as áreas em ordem alfabética, com quantas cientistas atuam em cada uma (inclusive áreas sem cientistas).

```json
[
  { "id": 3, "nome": "Biologia", "descricao": "Seres vivos, da molécula ao ecossistema.", "totalCientistas": 6 },
  { "id": 6, "nome": "Computação", "descricao": "Algoritmos, programação e sistemas.", "totalCientistas": 1 }
]
```

## POST /sugestoes

Grava no banco (tabela `sugestao_cientista`) a sugestão de uma nova cientista, com status `PENDENTE` e a data do dia.
Os espaços sobrando são retirados e o e-mail é gravado em letras minúsculas.

Corpo (JSON):

```json
{
  "nomeCientista": "Sonia Guimarães",
  "justificativa": "Primeira mulher negra doutora em física no Brasil.",
  "nomeSugestor": "Ana",
  "emailSugestor": "ana@exemplo.com",
  "consentimento": true
}
```

| Campo | Regra | Mensagem de erro |
|---|---|---|
| `nomeCientista` | Obrigatório, até 150 caracteres | `Informe o nome da cientista.` / `O nome da cientista deve ter no máximo 150 caracteres.` |
| `justificativa` | Obrigatória, de 20 a 2000 caracteres | `Conte por que ela deveria estar no portal.` / `A justificativa deve ter entre 20 e 2000 caracteres.` |
| `nomeSugestor` | Obrigatório, até 100 caracteres | `Informe o seu nome.` / `O seu nome deve ter no máximo 100 caracteres.` |
| `emailSugestor` | Obrigatório, e-mail válido, até 150 caracteres | `Informe o seu e-mail.` / `Informe um e-mail válido.` / `O e-mail deve ter no máximo 150 caracteres.` |
| `consentimento` | Precisa ser `true` (LGPD: o e-mail só é usado para responder à sugestão) | `É preciso concordar com o uso dos seus dados para responder à sugestão.` |

- **Sucesso:** `201 Created`. O e-mail não é devolvido. `dataSugestao` traz só a data (`AAAA-MM-DD`):

```json
{
  "id": 12,
  "nomeCientista": "Sonia Guimarães",
  "status": "PENDENTE",
  "dataSugestao": "2026-11-10",
  "mensagem": "Sugestão recebida! A equipe vai avaliar e responder pelo e-mail informado."
}
```

- **Dados inválidos:** `400 Bad Request` com a mensagem de cada campo (veja [Formato dos erros](#formato-dos-erros)).

Esta rota não exige login nem o token CSRF.

---

# Área da equipe

As rotas abaixo (menos `POST /auth/login`) exigem login. Sem sessão, a resposta é `401`.
As que alteram dados (`POST`, `PUT`, `PATCH` e `DELETE`) também exigem o token CSRF (veja [Segurança](#segurança)).

## POST /auth/login

Confere o e-mail e a senha e abre a sessão. O e-mail é comparado sem diferença entre maiúsculas e minúsculas.
Não exige o token CSRF.

```json
{ "email": "equipe@portal.local", "senha": "ciencia2026" }
```

- **Sucesso:** `200 OK` com os dados da pessoa (nunca a senha). A resposta traz o cookie da sessão.

```json
{ "id": 1, "nome": "Equipe do Portal", "email": "equipe@portal.local" }
```

- **E-mail ou senha errados:** `401` com `E-mail ou senha incorretos.` A mensagem não diz qual dos dois está errado.
- **Campo vazio:** `400` com `Informe o seu e-mail.` ou `Informe a sua senha.` em `campos`.

## GET /auth/eu

Mostra quem está logado. O site usa esta rota para saber se a sessão continua aberta.

- **Com sessão:** `200 OK` com `{ "id", "nome", "email" }`
- **Sem sessão:** `401` com `Entre com seu e-mail e senha para continuar.`

## POST /auth/logout

Encerra a sessão. Não tem corpo.

- **Sucesso:** `204 No Content`
- **Sem o token CSRF:** `403`

## GET /admin/sugestoes

Lista as sugestões, das mais recentes para as mais antigas. Sem filtro, traz todas.

| Parâmetro | Valores |
|---|---|
| `status` (opcional) | `PENDENTE`, `APROVADA` ou `RECUSADA`. Exemplo: `/admin/sugestoes?status=PENDENTE` |

```json
[
  {
    "id": 12,
    "nomeCientista": "Sonia Guimarães",
    "justificativa": "Primeira mulher negra doutora em física no Brasil.",
    "nomeSugestor": "Ana",
    "emailSugestor": "ana@exemplo.com",
    "dataSugestao": "2026-11-10",
    "status": "PENDENTE",
    "revisor": null,
    "idCientista": null
  }
]
```

| Campo | O que é |
|---|---|
| `emailSugestor` | Aparece só aqui, na área da equipe, e serve apenas para responder à sugestão |
| `revisor` | Nome da pessoa da equipe que avaliou (`null` se ainda não foi avaliada) |
| `idCientista` | Id da cientista cadastrada a partir desta sugestão (`null` se não houver) |

- **Sucesso:** `200 OK` (lista vazia `[]` se não houver sugestões)
- **Status inválido:** `400` com `Valor inválido em status.`

## PATCH /admin/sugestoes/{id}

Aprova ou recusa uma sugestão e registra quem decidiu.

```json
{ "status": "RECUSADA" }
```

| Campo | Regra | Mensagem de erro |
|---|---|---|
| `status` | Obrigatório: `APROVADA` ou `RECUSADA` | `Escolha aprovar ou recusar.` |

- Aprovar não cadastra a cientista. O cadastro é feito em `POST /admin/cientistas`, de preferência com `idSugestao`.
- Uma sugestão que já virou cadastro não pode ser recusada: `400` com `Esta sugestão já virou um cadastro e não pode ser recusada.`
- `PENDENTE` não é aceito: `400` com `Escolha aprovar ou recusar.`
- Valor fora da lista (ex.: `"TALVEZ"`): `400` com `Não foi possível ler os dados enviados.`

Respostas:

- **Sucesso:** `200 OK` com a sugestão atualizada, no mesmo formato de `GET /admin/sugestoes`.
- **Id inexistente:** `404` com `Sugestão não encontrada.`

---

# Segurança

## Quem acessa o quê

| Rotas | Acesso |
|---|---|
| `GET /cientistas`, `GET /cientistas/{id}`, `GET /areas` | Público |
| `POST /sugestoes`, `POST /auth/login` | Público, sem token CSRF |
| Todas as outras (`/auth/eu`, `/auth/logout`, `/admin/...`) | Só com login |

## Sessão

- O login abre uma sessão no servidor. O navegador guarda o identificador num cookie **HttpOnly** (o JavaScript não lê) e
  `SameSite=Lax`.
- No login, o identificador da sessão é trocado, para ninguém reaproveitar uma sessão antiga.
- `POST /auth/logout` encerra a sessão.

## Proteção CSRF

As ações que alteram dados na área da equipe precisam de um token, para impedir que outro site faça essas ações usando a
sessão de quem está logado.

- A API envia o token no cookie `XSRF-TOKEN`.
- O site devolve o mesmo valor no cabeçalho `X-XSRF-TOKEN`. O Angular (`HttpClient`) faz isso sozinho.
- `POST /sugestoes` e `POST /auth/login` não exigem o token, porque não dependem de sessão.
- Sem o token, a resposta é `403` com `Não foi possível confirmar esta ação. Recarregue a página e tente de novo.`
  Isso vale mesmo para quem não está logado.

## Senhas

- As senhas são guardadas com **BCrypt** (coluna `usuario.senha_hash`). Nenhuma resposta da API devolve a senha.
- A primeira conta da equipe é criada quando a API sobe e a tabela `usuario` está vazia, a partir das variáveis
  `ADMIN_EMAIL` e `ADMIN_SENHA` (`ADMIN_NOME` é opcional; o padrão é `Equipe do Portal`). Sem essas variáveis, nenhuma
  conta é criada. Nenhuma senha fica no código.
- No ambiente Docker, os valores de teste são `equipe@portal.local` / `ciencia2026`. Eles valem **só para o ambiente de teste
  local** (veja `docs/AMBIENTE.md`).

## Respostas 401 e 403

As duas usam o mesmo formato de erro das outras rotas:

```json
{ "status": 401, "mensagem": "Entre com seu e-mail e senha para continuar.", "campos": {} }
```

```json
{ "status": 403, "mensagem": "Não foi possível confirmar esta ação. Recarregue a página e tente de novo.", "campos": {} }
```

## Acesso pelo front-end (CORS)

No Docker e no `npm start`, o site chama a API pelo próprio endereço (`/api/...`), e o servidor do site repassa as chamadas
para a API. Assim o navegador fala com um endereço só, e o cookie da sessão funciona normalmente.

Para chamadas diretas de outro endereço, a API aceita os métodos `GET`, `POST`, `PUT`, `PATCH` e `DELETE` vindos de
`http://localhost:4200`. Para liberar outros endereços, defina a variável `CORS_ORIGENS` (separados por vírgula).

---

# Testar na mão

Com o banco criado (veja `docs/BANCO.md`) e a API rodando (`./mvnw spring-boot:run`):

```bash
# Parte pública
curl http://localhost:8080/cientistas
curl http://localhost:8080/cientistas/1

# Área da equipe: entra, guarda o cookie da sessão e consulta as sugestões pendentes
curl -c cookies.txt -H "Content-Type: application/json" \
  -d '{"email":"equipe@portal.local","senha":"ciencia2026"}' \
  http://localhost:8080/auth/login
curl -b cookies.txt "http://localhost:8080/admin/sugestoes?status=PENDENTE"
```

Use o e-mail e a senha definidos em `ADMIN_EMAIL` e `ADMIN_SENHA`. As consultas (`GET`) não precisam do token CSRF.
