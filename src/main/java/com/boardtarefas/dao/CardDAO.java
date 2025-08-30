package com.boardtarefas.dao;

import com.boardtarefas.model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public void salvar(Card card) throws SQLException {
        String sql = "INSERT INTO cards (coluna_id, titulo, descricao, data_entrada_coluna) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, card.getColunaId());
            stmt.setString(2, card.getTitulo());
            stmt.setString(3, card.getDescricao());
            stmt.setTimestamp(4, Timestamp.valueOf(card.getDataEntradaColuna()));
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    card.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Card card) throws SQLException {
        String sql = "UPDATE cards SET coluna_id = ?, titulo = ?, descricao = ?, " +
                    "data_entrada_coluna = ?, bloqueado = ?, motivo_bloqueio = ?, " +
                    "data_bloqueio = ?, motivo_desbloqueio = ?, data_desbloqueio = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, card.getColunaId());
            stmt.setString(2, card.getTitulo());
            stmt.setString(3, card.getDescricao());
            stmt.setTimestamp(4, Timestamp.valueOf(card.getDataEntradaColuna()));
            stmt.setBoolean(5, card.isBloqueado());
            stmt.setString(6, card.getMotivoBloqueio());
            
            if (card.getDataBloqueio() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(card.getDataBloqueio()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            
            stmt.setString(8, card.getMotivoDesbloqueio());
            
            if (card.getDataDesbloqueio() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(card.getDataDesbloqueio()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            
            stmt.setInt(10, card.getId());
            stmt.executeUpdate();
        }
    }

    public List<Card> buscarPorColuna(int colunaId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE coluna_id = ? ORDER BY data_criacao";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, colunaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card card = criarCardDoResultSet(rs);
                    cards.add(card);
                }
            }
        }
        
        return cards;
    }

    public Card buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cards WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarCardDoResultSet(rs);
                }
            }
        }
        
        return null;
    }

    public void moverCard(int cardId, int novaColunaId) throws SQLException {
        String sql = "UPDATE cards SET coluna_id = ?, data_entrada_coluna = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, novaColunaId);
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(3, cardId);
            stmt.executeUpdate();
        }
    }

    public void registrarMovimentacao(int cardId, int colunaOrigemId, int colunaDestinoId) throws SQLException {
        String sql = "INSERT INTO historico_movimentacao (card_id, coluna_origem_id, coluna_destino_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cardId);
            if (colunaOrigemId > 0) {
                stmt.setInt(2, colunaOrigemId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, colunaDestinoId);
            stmt.executeUpdate();
        }
    }

    public void registrarBloqueio(int cardId, String motivo) throws SQLException {
        String sql = "INSERT INTO bloqueios (card_id, motivo_bloqueio) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cardId);
            stmt.setString(2, motivo);
            stmt.executeUpdate();
        }
    }

    public void registrarDesbloqueio(int cardId, String motivo) throws SQLException {
        String sql = "UPDATE bloqueios SET motivo_desbloqueio = ?, data_desbloqueio = ? " +
                    "WHERE card_id = ? AND data_desbloqueio IS NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, motivo);
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(3, cardId);
            stmt.executeUpdate();
        }
    }

    public List<Card> buscarCardsPorBoard(int boardId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT c.* FROM cards c " +
                    "INNER JOIN colunas col ON c.coluna_id = col.id " +
                    "WHERE col.board_id = ? ORDER BY col.ordem_coluna, c.data_criacao";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card card = criarCardDoResultSet(rs);
                    cards.add(card);
                }
            }
        }
        
        return cards;
    }

    private Card criarCardDoResultSet(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setId(rs.getInt("id"));
        card.setColunaId(rs.getInt("coluna_id"));
        card.setTitulo(rs.getString("titulo"));
        card.setDescricao(rs.getString("descricao"));
        card.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        card.setDataEntradaColuna(rs.getTimestamp("data_entrada_coluna").toLocalDateTime());
        card.setBloqueado(rs.getBoolean("bloqueado"));
        card.setMotivoBloqueio(rs.getString("motivo_bloqueio"));
        
        Timestamp dataBloqueio = rs.getTimestamp("data_bloqueio");
        if (dataBloqueio != null) {
            card.setDataBloqueio(dataBloqueio.toLocalDateTime());
        }
        
        card.setMotivoDesbloqueio(rs.getString("motivo_desbloqueio"));
        
        Timestamp dataDesbloqueio = rs.getTimestamp("data_desbloqueio");
        if (dataDesbloqueio != null) {
            card.setDataDesbloqueio(dataDesbloqueio.toLocalDateTime());
        }
        
        return card;
    }
}