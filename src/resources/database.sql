CREATE DATABASE IF NOT EXISTS board_tarefas;
USE board_tarefas;

CREATE TABLE boards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE colunas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    ordem_coluna INT NOT NULL,
    tipo ENUM('INICIAL', 'PENDENTE', 'FINAL', 'CANCELAMENTO') NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    UNIQUE KEY unique_board_ordem (board_id, ordem_coluna),
    UNIQUE KEY unique_board_tipo_inicial (board_id, tipo) 
);

CREATE TABLE cards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coluna_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_entrada_coluna TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    bloqueado BOOLEAN DEFAULT FALSE,
    motivo_bloqueio TEXT,
    data_bloqueio TIMESTAMP NULL,
    motivo_desbloqueio TEXT,
    data_desbloqueio TIMESTAMP NULL,
    FOREIGN KEY (coluna_id) REFERENCES colunas(id) ON DELETE CASCADE
);

CREATE TABLE historico_movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    card_id INT NOT NULL,
    coluna_origem_id INT,
    coluna_destino_id INT NOT NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
    FOREIGN KEY (coluna_origem_id) REFERENCES colunas(id) ON DELETE SET NULL,
    FOREIGN KEY (coluna_destino_id) REFERENCES colunas(id) ON DELETE CASCADE
);

CREATE TABLE bloqueios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    card_id INT NOT NULL,
    motivo_bloqueio TEXT NOT NULL,
    data_bloqueio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo_desbloqueio TEXT,
    data_desbloqueio TIMESTAMP NULL,
    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);