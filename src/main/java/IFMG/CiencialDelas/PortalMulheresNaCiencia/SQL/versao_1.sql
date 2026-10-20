CREATE DATABASE IF NOT EXISTS "Portal_Mulheres";
USE Portal_Mulheres;
CREATE TABLE IF NOT EXISTS Cientistas(id_cientista INT NOT NULL, nome VARCHAR(45), biografia TEXT, intstituicao_principal VARCHAR(45), data_nascimento DATE,data_falescimento DATE, nacionalidade VARCHAR(45), id_sugestao_cientista INT, PRIMARY KEY(id_cientista));
CREATE TABLE IF NOT EXISTS area_atuacao(id_area_atuacao INT PRIMARY KEY, nome VARCHAR(45), descricao VARCHAR(45));
CREATE TABLE IF NOT EXISTS cientista_area_atuacao(id_cientista NOT NULL,id_area_atuacao NOT NULL, PRIMARY KEY (id_cientista, id_area_atuacao), foreign key (id_cientista) references Cientistas(id_cientista),foreign key(id_area_atuacao) references area_atuacao(id_area_atuacao));