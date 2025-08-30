package com.boardtarefas.dao;

import com.boardtarefas.model.Coluna;
import com.boardtarefas.model.TipoColuna;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColunaDAO {

    public void salvar(Coluna coluna) throws SQLException {
        String sql = "INSERT INTO colunas (board_id, nome, ordem_coluna, tipo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, coluna.getBoardId());
            stmt.setString(2, coluna.getNome());
            stmt.setInt(3, coluna.getOrdemColuna());
            stmt.setString(4, coluna.getTipo().name());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    coluna.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Coluna> buscarPorBoard(int boardId) throws SQLException {
        List<Coluna> colunas = new ArrayList<>();
        String sql = "SELECT * FROM colunas WHERE board_id = ? ORDER BY ordem_coluna";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Coluna coluna = new Coluna();
                    coluna.setId(rs.getInt("id"));
                    coluna.setBoardId(rs.getInt("board_id"));
                    coluna.setNome(rs.getString("nome"));
                    coluna.setOrdemColuna(rs.getInt("ordem_coluna"));
                    coluna.setTipo(TipoColuna.valueOf(rs.getString("tipo")));
                    colunas.add(coluna);
                }
            }
        }
        
        return colunas;
    }

    public Coluna buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM colunas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Coluna coluna = new Coluna();
                    coluna.setId(rs.getInt("id"));
                    coluna.setBoardId(rs.getInt("board_id"));
                    coluna.setNome(rs.getString("nome"));
                    coluna.setOrdemColuna(rs.getInt("ordem_coluna"));
                    coluna.setTipo(TipoColuna.valueOf(rs.getString("tipo")));
                    return coluna;
                }
            }
        }
        
        return null;
    }

    public Coluna buscarProximaColuna(int boardId, int ordemAtual) throws SQLException {
        String sql = "SELECT * FROM colunas WHERE board_id = ? AND ordem_coluna = ? + 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            stmt.setInt(2, ordemAtual);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Coluna coluna = new Coluna();
                    coluna.setId(rs.getInt("id"));
                    coluna.setBoardId(rs.getInt("board_id"));
                    coluna.setNome(rs.getString("nome"));
                    coluna.setOrdemColuna(rs.getInt("ordem_coluna"));
                    coluna.setTipo(TipoColuna.valueOf(rs.getString("tipo")));
                    return coluna;
                }
            }
        }
        
        return null;
    }

    public Coluna buscarColunaCancelamento(int boardId) throws SQLException {
        String sql = "SELECT * FROM colunas WHERE board_id = ? AND tipo = 'CANCELAMENTO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Coluna coluna = new Coluna();
                    coluna.setId(rs.getInt("id"));
                    coluna.setBoardId(rs.getInt("board_id"));
                    coluna.setNome(rs.getString("nome"));
                    coluna.setOrdemColuna(rs.getInt("ordem_coluna"));
                    coluna.setTipo(TipoColuna.valueOf(rs.getString("tipo")));
                    return coluna;
                }
            }
        }
        
        return null;
    }

    public Coluna buscarColunaInicial(int boardId) throws SQLException {
        String sql = "SELECT * FROM colunas WHERE board_id = ? AND tipo = 'INICIAL'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Coluna coluna = new Coluna();
                    coluna.setId(rs.getInt("id"));
                    coluna.setBoardId(rs.getInt("board_id"));
                    coluna.setNome(rs.getString("nome"));
                    coluna.setOrdemColuna(rs.getInt("ordem_coluna"));
                    coluna.setTipo(TipoColuna.valueOf(rs.getString("tipo")));
                    return coluna;
                }
            }
        }
        
        return null;
    }

    public boolean existeTipoColuna(int boardId, TipoColuna tipo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM colunas WHERE board_id = ? AND tipo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            stmt.setString(2, tipo.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
}