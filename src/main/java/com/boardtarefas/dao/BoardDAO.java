package com.boardtarefas.dao;

import com.boardtarefas.model.Board;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {

    public void salvar(Board board) throws SQLException {
        String sql = "INSERT INTO boards (nome) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, board.getNome());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    board.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Board> listarTodos() throws SQLException {
        List<Board> boards = new ArrayList<>();
        String sql = "SELECT * FROM boards ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Board board = new Board();
                board.setId(rs.getInt("id"));
                board.setNome(rs.getString("nome"));
                board.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                boards.add(board);
            }
        }
        
        return boards;
    }

    public Board buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM boards WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Board board = new Board();
                    board.setId(rs.getInt("id"));
                    board.setNome(rs.getString("nome"));
                    board.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                    return board;
                }
            }
        }
        
        return null;
    }

    public Board buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM boards WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Board board = new Board();
                    board.setId(rs.getInt("id"));
                    board.setNome(rs.getString("nome"));
                    board.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                    return board;
                }
            }
        }
        
        return null;
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM boards WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean existeBoard(String nome) throws SQLException {
        String sql = "SELECT COUNT(*) FROM boards WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
}