DROP DATABASE Portal_Mulheres;
CREATE DATABASE IF NOT EXISTS Portal_Mulheres;

USE Portal_Mulheres;

CREATE TABLE IF NOT EXISTS Cientistas(id_cientista INT NOT NULL, nome VARCHAR(45), biografia TEXT, intstituicao_principal VARCHAR(45),
data_nascimento DATE,data_falescimento DATE, nacionalidade VARCHAR(45), id_sugestao_cientista INT, PRIMARY KEY(id_cientista));

CREATE TABLE IF NOT EXISTS area_atuacao(id_area_atuacao INT PRIMARY KEY, nome VARCHAR(45), descricao VARCHAR(45));

CREATE TABLE IF NOT EXISTS cientista_area_atuacao(id_cientista INT NULL,id_area_atuacao INT NOT NULL, PRIMARY KEY (id_cientista, id_area_atuacao),
foreign key (id_cientista) references Cientistas(id_cientista),foreign key(id_area_atuacao) references area_atuacao(id_area_atuacao));

CREATE TABLE IF NOT EXISTS premio(id_premio INT PRIMARY KEY, nome VARCHAR(45), institucao VARCHAR(45));

CREATE TABLE IF NOT EXISTS cientista_tem_premio(cientista_id_cientista INT NOT NULL, premio_id_premio INT NOT NULL, PRIMARY KEY (cientista_id_cientista, premio_id_premio), recebido_em DATE,
foreign key (cientista_id_cientista) references Cientistas(id_cientista), foreign key(premio_id_premio) references premio(id_premio));

CREATE TABLE IF NOT EXISTS contribuicao(id_contribuicao INT PRIMARY KEY, titulo VARCHAR(45), descricao VARCHAR(45), ano INT, link_referencia VARCHAR(45));

CREATE TABLE IF NOT EXISTS cientista_tem_contribuicao(cientista_id_cientista INT NOT NULL, contribuicao_id_contribuicao INT NOT NULL, PRIMARY KEY(cientista_id_cientista,contribuicao_id_contribuicao),
foreign key (cientista_id_cientista) references Cientistas(id_cientista), foreign key (contribuicao_id_contribuicao) references contribuicao(id_contribuicao));
