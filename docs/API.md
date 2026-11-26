# Endpoints da API

A API responde sempre em JSON. Datas usam o formato `AAAA-MM-DD`.

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/cientistas` | Público | Lista resumida de todas as cientistas (cards do Catálogo) |
| `GET` | `/cientistas/{id}` | Público | Dados completos de uma cientista (página de Perfil) |
| `GET` | `/areas` | Público | Áreas de atuação com o total de cientistas (filtros e página inicial) |

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

---

# Testar na mão

Com o banco criado (veja `docs/BANCO.md`) e a API rodando (`./mvnw spring-boot:run`):

```bash
# Parte pública
curl http://localhost:8080/cientistas
curl http://localhost:8080/cientistas/1

```
