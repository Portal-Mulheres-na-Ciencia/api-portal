-- Dados de exemplo do Portal Mulheres na Ciência (os mesmos mostrados no site).
-- INSERT IGNORE permite rodar o script mais de uma vez sem duplicar registros.

-- Garante que os acentos sejam gravados corretamente, qualquer que seja o padrão do cliente mysql.
SET NAMES utf8mb4;

USE portal_mulheres;

INSERT IGNORE INTO area_atuacao (id_area_atuacao, nome, descricao) VALUES
  (1, 'Física', 'Matéria, energia e as leis da natureza.'),
  (2, 'Química', 'Composição e transformação das substâncias.'),
  (3, 'Biologia', 'Seres vivos, da molécula ao ecossistema.'),
  (4, 'Saúde', 'Medicina, genética e saúde pública.'),
  (5, 'Matemática', 'Estruturas, padrões e raciocínio formal.'),
  (6, 'Computação', 'Algoritmos, programação e sistemas.');

INSERT IGNORE INTO cientista
  (id_cientista, nome, biografia, instituicao_principal, data_nascimento, data_falescimento, nacionalidade)
VALUES
  (1, 'Marie Curie',
   'Física e química nascida em Varsóvia e radicada na França. Pioneira no estudo da radioatividade — termo que ela mesma cunhou —, foi a primeira mulher a receber um Prêmio Nobel e a única pessoa premiada em duas ciências diferentes.',
   'Universidade de Paris', '1867-11-07', '1934-07-04', 'Polonesa e francesa'),
  (2, 'Bertha Lutz',
   'Bióloga especializada em anfíbios, atuou por décadas no Museu Nacional. Foi uma das principais lideranças do movimento pelo voto feminino no Brasil e defendeu a igualdade de gênero na fundação das Nações Unidas.',
   'Museu Nacional', '1894-08-02', '1976-09-16', 'Brasileira'),
  (3, 'Johanna Döbereiner',
   'Agrônoma nascida na antiga Tchecoslováquia e naturalizada brasileira. Suas pesquisas sobre bactérias fixadoras de nitrogênio permitiram cultivar soja no Brasil com menos adubo químico.',
   'Embrapa Agrobiologia', '1924-11-28', '2000-10-05', 'Brasileira'),
  (4, 'Nise da Silveira',
   'Psiquiatra alagoana, recusou tratamentos agressivos como o eletrochoque e propôs o cuidado por meio da arte e do afeto. Sua obra transformou a forma como o Brasil trata a saúde mental.',
   'Centro Psiquiátrico Pedro II', '1905-02-15', '1999-10-30', 'Brasileira'),
  (5, 'Graziela Barroso',
   'Botânica sul-mato-grossense, começou a carreira científica aos 30 anos e se tornou referência na classificação de plantas brasileiras, formando gerações de taxonomistas.',
   'Jardim Botânico do Rio de Janeiro', '1912-04-11', '2003-05-05', 'Brasileira'),
  (6, 'Mayana Zatz',
   'Geneticista, coordena o Centro de Estudos do Genoma Humano e Células-Tronco da USP. É referência mundial no estudo de doenças neuromusculares, como as distrofias musculares.',
   'Universidade de São Paulo', '1947-07-16', NULL, 'Brasileira'),
  (7, 'Jaqueline Goes de Jesus',
   'Biomédica baiana, integrou a equipe que sequenciou o genoma do novo coronavírus no Brasil apenas 48 horas após a confirmação do primeiro caso no país.',
   'Instituto de Medicina Tropical da USP', '1989-01-01', NULL, 'Brasileira'),
  (8, 'Rosalind Franklin',
   'Química britânica especialista em difração de raios X. Suas imagens do DNA foram decisivas para a descoberta da estrutura em dupla hélice.',
   'King''s College London', '1920-07-25', '1958-04-16', 'Britânica'),
  (9, 'Ada Lovelace',
   'Matemática inglesa, escreveu notas sobre a Máquina Analítica de Charles Babbage nas quais descreveu o que é considerado o primeiro algoritmo feito para ser executado por uma máquina.',
   'Pesquisadora independente', '1815-12-10', '1852-11-27', 'Britânica'),
  (10, 'Katherine Johnson',
   'Matemática da NASA, calculou trajetórias de voos espaciais tripulados dos Estados Unidos. Sua história ganhou destaque com o livro e o filme “Estrelas Além do Tempo”.',
   'NASA', '1918-08-26', '2020-02-24', 'Estadunidense'),
  (11, 'Emmy Noether',
   'Matemática alemã, fundamental para a álgebra abstrata. O teorema que leva seu nome liga simetrias da natureza a leis de conservação e é base da física moderna.',
   'Universidade de Göttingen', '1882-03-23', '1935-04-14', 'Alemã');

INSERT IGNORE INTO cientista_tem_area_atuacao (cientista_id_cientista, area_atuacao_id_area_atuacao) VALUES
  (1, 1),
  (1, 2),
  (2, 3),
  (3, 3),
  (4, 4),
  (5, 3),
  (6, 3),
  (6, 4),
  (7, 4),
  (7, 3),
  (8, 2),
  (8, 3),
  (9, 5),
  (9, 6),
  (10, 5),
  (11, 5),
  (11, 1);

INSERT IGNORE INTO premio (id_premio, nome, instituicao) VALUES
  (1, 'Prêmio Nobel de Física', 'Academia Real das Ciências da Suécia'),
  (2, 'Prêmio Nobel de Química', 'Academia Real das Ciências da Suécia'),
  (3, 'Prêmio UNESCO de Ciência', 'UNESCO'),
  (4, 'Prêmio L''Oréal-UNESCO para Mulheres na Ciência', 'Fundação L''Oréal e UNESCO'),
  (5, 'Medalha Presidencial da Liberdade', 'Presidência dos Estados Unidos');

-- Só o ano do prêmio é conhecido; a data usa 1º de janeiro e a API mostra apenas o ano.
INSERT IGNORE INTO cientista_tem_premio (cientista_id_cientista, premio_id_premio, recebido_em) VALUES
  (1, 1, '1903-01-01'),
  (1, 2, '1911-01-01'),
  (3, 3, '1989-01-01'),
  (6, 4, '2001-01-01'),
  (10, 5, '2015-01-01');

INSERT IGNORE INTO contribuicao (id_contribuicao, titulo, descricao, ano, link_referencia) VALUES
  (1, 'Descoberta do polônio e do rádio',
   'Com Pierre Curie, identificou dois novos elementos químicos a partir do minério de urânio.',
   1898, 'https://www.nobelprize.org/prizes/physics/1903/marie-curie/biographical/'),
  (2, 'Isolamento do rádio metálico',
   'Obteve o rádio em forma pura, confirmando definitivamente sua existência como elemento.',
   1910, 'https://www.nobelprize.org/prizes/chemistry/1911/marie-curie/facts/'),
  (3, 'Ingresso no Museu Nacional',
   'Aprovada em concurso público, tornou-se uma das primeiras mulheres no serviço público brasileiro.',
   1919, NULL),
  (4, 'Igualdade de gênero na Carta da ONU',
   'Como delegada do Brasil na Conferência de São Francisco, defendeu a menção à igualdade entre homens e mulheres.',
   1945, NULL),
  (5, 'Bactérias fixadoras de nitrogênio em gramíneas',
   'Demonstrou a associação entre bactérias do solo e gramíneas tropicais na fixação biológica de nitrogênio.',
   1976, NULL),
  (6, 'Seção de Terapêutica Ocupacional',
   'Criou ateliês de pintura e modelagem como forma de tratamento no hospital do Engenho de Dentro.',
   1946, NULL),
  (7, 'Museu de Imagens do Inconsciente',
   'Fundou o museu que reúne e estuda a produção artística dos pacientes.',
   1952, NULL),
  (8, 'Sistemática de Angiospermas do Brasil',
   'Primeiro volume da obra de referência sobre a classificação das plantas com flores do país.',
   1978, NULL),
  (9, 'Centro de Estudos do Genoma Humano',
   'Coordena o centro que pesquisa doenças genéticas e oferece aconselhamento a famílias.',
   2000, NULL),
  (10, 'Sequenciamento do SARS-CoV-2 no Brasil',
   'Sequenciamento rápido que ajudou a acompanhar a chegada e a disseminação do vírus.',
   2020, NULL),
  (11, 'Fotografia 51',
   'Imagem de difração de raios X que revelou a forma helicoidal do DNA.',
   1952, NULL),
  (12, 'Artigo sobre a estrutura do DNA na Nature',
   'Publicado na mesma edição dos artigos de Watson, Crick e Wilkins.',
   1953, NULL),
  (13, 'Notas sobre a Máquina Analítica',
   'Inclui um algoritmo para calcular números de Bernoulli e a ideia de máquinas que vão além do cálculo.',
   1843, NULL),
  (14, 'Trajetória do voo de Alan Shepard',
   'Análise de trajetória do primeiro voo espacial tripulado dos Estados Unidos.',
   1961, NULL),
  (15, 'Verificação do voo orbital de John Glenn',
   'Conferiu à mão os cálculos de computador da primeira órbita tripulada estadunidense.',
   1962, NULL),
  (16, 'Teorema de Noether',
   'Mostra que cada simetria contínua de um sistema físico corresponde a uma grandeza conservada.',
   1918, NULL);

-- Qual cientista fez cada contribuição.
INSERT IGNORE INTO cientista_tem_contribuicao (cientista_id_cientista, contribuicao_id_contribuicao) VALUES
  (1, 1),
  (1, 2),
  (2, 3),
  (2, 4),
  (3, 5),
  (4, 6),
  (4, 7),
  (5, 8),
  (6, 9),
  (7, 10),
  (8, 11),
  (8, 12),
  (9, 13),
  (10, 14),
  (10, 15),
  (11, 16);
