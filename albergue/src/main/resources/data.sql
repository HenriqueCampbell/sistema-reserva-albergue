INSERT INTO quarto (numero, capacidade, descricao, banheiro)
SELECT dados.numero, dados.capacidade, dados.descricao, dados.banheiro
FROM (
    SELECT 101 AS numero, 4 AS capacidade, 'Quarto com banheiro privativo' AS descricao, TRUE AS banheiro
    UNION ALL
    SELECT 102, 8, 'Quarto sem banheiro privativo', FALSE
    UNION ALL
    SELECT 103, 12, 'Quarto amplo com banheiro privativo', TRUE
) dados
WHERE NOT EXISTS (
    SELECT 1 FROM quarto existente WHERE existente.numero = dados.numero
);

UPDATE quarto SET banheiro = TRUE WHERE numero = 101;
UPDATE quarto SET banheiro = FALSE WHERE numero = 102;
UPDATE quarto SET banheiro = TRUE WHERE numero = 103;

INSERT INTO leito (descricao, quarto_id, valor)
SELECT dados.descricao, quarto.id, dados.valor
FROM (
    SELECT 101 AS numero, 'Beliche superior, perto da janela, com sol da manhã' AS descricao, 75.00 AS valor
    UNION ALL SELECT 101, 'Beliche inferior, perto da porta', 70.00
    UNION ALL SELECT 101, 'Beliche superior, sol da manhã', 75.00
    UNION ALL SELECT 101, 'Beliche inferior, próximo ao banheiro', 70.00
    UNION ALL SELECT 102, 'Beliche superior, perto da janela', 60.00
    UNION ALL SELECT 102, 'Beliche inferior, perto da porta, sem sol direto', 55.00
    UNION ALL SELECT 102, 'Beliche superior, com sol da manhã', 60.00
    UNION ALL SELECT 102, 'Beliche inferior, área mais silenciosa', 55.00
    UNION ALL SELECT 102, 'Beliche superior, perto da porta', 60.00
    UNION ALL SELECT 102, 'Beliche inferior, perto da janela', 55.00
    UNION ALL SELECT 102, 'Beliche superior, sem sol direto', 60.00
    UNION ALL SELECT 102, 'Beliche inferior, no fundo do quarto', 55.00
    UNION ALL SELECT 103, 'Beliche superior, perto da janela, sol da manhã', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, perto da janela', 60.00
    UNION ALL SELECT 103, 'Beliche superior, perto da porta', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, perto da porta', 60.00
    UNION ALL SELECT 103, 'Beliche superior, área silenciosa', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, sem sol direto', 60.00
    UNION ALL SELECT 103, 'Beliche superior, sol da manhã', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, perto do banheiro', 60.00
    UNION ALL SELECT 103, 'Beliche superior, no fundo do quarto', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, área central', 60.00
    UNION ALL SELECT 103, 'Beliche superior, perto da porta e com sol', 65.00
    UNION ALL SELECT 103, 'Beliche inferior, perto da janela e silencioso', 60.00
) dados
JOIN quarto ON quarto.numero = dados.numero
WHERE NOT EXISTS (
    SELECT 1 FROM leito existente
    WHERE existente.quarto_id = quarto.id
      AND existente.descricao = dados.descricao
);
