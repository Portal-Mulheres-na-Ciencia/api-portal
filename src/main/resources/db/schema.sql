-- Estrutura do banco do Portal Mulheres na Ciência, conforme o modelo relacional entregue em Banco de Dados I.
-- Nomes de tabelas e colunas iguais ao modelo. Os tipos ajustados estão explicados em docs/BANCO.md.
-- Pode ser executado mais de uma vez: não apaga dados existentes.
-- Chaves estrangeiras: as tabelas de ligação (cientista_tem_...) apagam e atualizam em cascata junto com a
-- cientista, a área, o prêmio ou a contribuição; as ligações opcionais (revisor da sugestão e sugestão de
-- origem da cientista) ficam NULL quando o registro apontado é apagado.

-- Garante que os acentos sejam gravados corretamente, qualquer que seja o padrão do cliente mysql.
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS portal_mulheres
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE portal_mulheres;

-- Pessoa da equipe que entra na área administrativa e avalia as sugestões.
CREATE TABLE IF NOT EXISTS usuario (
  id_usuario   INT          NOT NULL AUTO_INCREMENT,
  nome         VARCHAR(45)  NOT NULL,
  email        VARCHAR(150) NOT NULL,
  senha_hash   VARCHAR(255) NOT NULL,
  data_criacao DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uk_usuario_email (email)
);

-- Sugestão de cientista enviada por um visitante pelo site.
CREATE TABLE IF NOT EXISTS sugestao_cientista (
  id_sugestao_cientista INT          NOT NULL AUTO_INCREMENT,
  nome_cientista        VARCHAR(150) NOT NULL,
  justificativa         TEXT         NOT NULL,
  nome_sugestor         VARCHAR(100) NOT NULL,
  email_sugestor        VARCHAR(150) NOT NULL,
  data_sugestao         DATE         NOT NULL,
  status                VARCHAR(45)  NOT NULL DEFAULT 'PENDENTE',
  id_usuario_revisor    INT          NULL,
  PRIMARY KEY (id_sugestao_cientista),
  CONSTRAINT ck_sugestao_status CHECK (status IN ('PENDENTE', 'APROVADA', 'RECUSADA')),
  CONSTRAINT fk_sugestao_usuario FOREIGN KEY (id_usuario_revisor) REFERENCES usuario (id_usuario)
    ON DELETE SET NULL ON UPDATE CASCADE
);

-- Cientista exibida no portal. Quando foi cadastrada a partir de uma sugestão, guarda qual foi.
CREATE TABLE IF NOT EXISTS cientista (
  id_cientista          INT          NOT NULL AUTO_INCREMENT,
  nome                  VARCHAR(150) NOT NULL,
  biografia             TEXT,
  instituicao_principal VARCHAR(150),
  data_nascimento       DATE,
  data_falescimento     DATE,
  nacionalidade         VARCHAR(45),
  id_sugestao_cientista INT          NULL,
  PRIMARY KEY (id_cientista),
  UNIQUE KEY uk_cientista_sugestao (id_sugestao_cientista),
  CONSTRAINT fk_cientista_sugestao FOREIGN KEY (id_sugestao_cientista)
    REFERENCES sugestao_cientista (id_sugestao_cientista)
    ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS area_atuacao (
  id_area_atuacao INT         NOT NULL AUTO_INCREMENT,
  nome            VARCHAR(45) NOT NULL,
  descricao       VARCHAR(45),
  PRIMARY KEY (id_area_atuacao),
  UNIQUE KEY uk_area_nome (nome)
);

-- Uma cientista atua em várias áreas e uma área tem várias cientistas.
CREATE TABLE IF NOT EXISTS cientista_tem_area_atuacao (
  cientista_id_cientista       INT NOT NULL,
  area_atuacao_id_area_atuacao INT NOT NULL,
  PRIMARY KEY (cientista_id_cientista, area_atuacao_id_area_atuacao),
  CONSTRAINT fk_ctaa_cientista FOREIGN KEY (cientista_id_cientista) REFERENCES cientista (id_cientista)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ctaa_area FOREIGN KEY (area_atuacao_id_area_atuacao) REFERENCES area_atuacao (id_area_atuacao)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS premio (
  id_premio   INT          NOT NULL AUTO_INCREMENT,
  nome        VARCHAR(150) NOT NULL,
  instituicao VARCHAR(45),
  PRIMARY KEY (id_premio)
);

-- Prêmios recebidos por cada cientista e a data em que recebeu.
CREATE TABLE IF NOT EXISTS cientista_tem_premio (
  cientista_id_cientista INT  NOT NULL,
  premio_id_premio       INT  NOT NULL,
  recebido_em            DATE NOT NULL,
  PRIMARY KEY (cientista_id_cientista, premio_id_premio),
  CONSTRAINT fk_ctp_cientista FOREIGN KEY (cientista_id_cientista) REFERENCES cientista (id_cientista)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ctp_premio FOREIGN KEY (premio_id_premio) REFERENCES premio (id_premio)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS contribuicao (
  id_contribuicao INT          NOT NULL AUTO_INCREMENT,
  titulo          VARCHAR(200) NOT NULL,
  descricao       TEXT,
  ano             INT,
  link_referencia VARCHAR(500),
  PRIMARY KEY (id_contribuicao)
);

-- Uma contribuição pode ter mais de uma cientista, e uma cientista tem várias contribuições.
CREATE TABLE IF NOT EXISTS cientista_tem_contribuicao (
  cientista_id_cientista       INT NOT NULL,
  contribuicao_id_contribuicao INT NOT NULL,
  PRIMARY KEY (cientista_id_cientista, contribuicao_id_contribuicao),
  CONSTRAINT fk_ctc_cientista FOREIGN KEY (cientista_id_cientista) REFERENCES cientista (id_cientista)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ctc_contribuicao FOREIGN KEY (contribuicao_id_contribuicao) REFERENCES contribuicao (id_contribuicao)
    ON DELETE CASCADE ON UPDATE CASCADE
);
