package com.boardtarefas.service;

import com.boardtarefas.dao.CardDAO;
import com.boardtarefas.dao.ColunaDAO;
import com.boardtarefas.model.Board;
import com.boardtarefas.model.Card;
import com.boardtarefas.model.Coluna;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {
    private CardDAO cardDAO;
    private ColunaDAO colunaDAO;

    public RelatorioService() {
        this.cardDAO = new CardDAO();
        this.colunaDAO = new ColunaDAO();
    }

    public void gerarRelatorioTempo(Board board) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("RELATÓRIO DE TEMPO - BOARD: " + board.getNome().toUpperCase());
            System.out.println("=".repeat(80));

            List<Card> cardsFinalizados = buscarCardsFinalizados(board.getId());
            
            if (cardsFinalizados.isEmpty()) {
                System.out.println("Nenhum card finalizado encontrado.");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Card card : cardsFinalizados) {
                System.out.println("\n📋 CARD: " + card.getTitulo());
                System.out.println("-".repeat(50));
                System.out.println("Descrição: " + card.getDescricao());
                System.out.println("Data de Criação: " + card.getDataCriacao().format(formatter));

                // Buscar histórico de movimentações
                List<MovimentacaoInfo> movimentacoes = buscarHistoricoMovimentacao(card.getId());
                
                LocalDateTime inicioCard = card.getDataCriacao();
                LocalDateTime fimCard = null;
                long tempoTotalMinutos = 0;

                System.out.println("\n📊 HISTÓRICO DE MOVIMENTAÇÕES:");
                
                for (int i = 0; i < movimentacoes.size(); i++) {
                    MovimentacaoInfo mov = movimentacoes.get(i);
                    
                    if (i == movimentacoes.size() - 1) {
                        fimCard = mov.dataMovimentacao;
                    }

                    LocalDateTime inicioNaColuna = (i == 0) ? inicioCard : movimentacoes.get(i-1).dataMovimentacao;
                    long tempoNaColuna = Duration.between(inicioNaColuna, mov.dataMovimentacao).toMinutes();
                    tempoTotalMinutos += tempoNaColuna;

                    System.out.println("   → " + mov.nomeColuna + 
                                     " (Tempo: " + formatarTempo(tempoNaColuna) + ")");
                    System.out.println("     Data: " + mov.dataMovimentacao.format(formatter));
                }

                if (fimCard != null) {
                    long tempoTotalCard = Duration.between(inicioCard, fimCard).toMinutes();
                    System.out.println("\n⏱️  TEMPO TOTAL DO CARD: " + formatarTempo(tempoTotalCard));
                }

                System.out.println("\n" + "-".repeat(50));
            }

            // Estatísticas gerais
            gerarEstatisticasGerais(cardsFinalizados);

        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório de tempo: " + e.getMessage());
        }
    }

    public void gerarRelatorioBloqueios(Board board) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("RELATÓRIO DE BLOQUEIOS - BOARD: " + board.getNome().toUpperCase());
            System.out.println("=".repeat(80));

            List<BloqueioInfo> bloqueios = buscarHistoricoBloqueios(board.getId());
            
            if (bloqueios.isEmpty()) {
                System.out.println("Nenhum bloqueio encontrado.");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            String cardAtual = "";
            for (BloqueioInfo bloqueio : bloqueios) {
                if (!bloqueio.tituloCard.equals(cardAtual)) {
                    cardAtual = bloqueio.tituloCard;
                    System.out.println("\n📋 CARD: " + cardAtual);
                    System.out.println("-".repeat(50));
                }

                System.out.println("\n🔒 BLOQUEIO:");
                System.out.println("   Motivo: " + bloqueio.motivoBloqueio);
                System.out.println("   Data do Bloqueio: " + bloqueio.dataBloqueio.format(formatter));
                
                if (bloqueio.dataDesbloqueio != null) {
                    long tempoBloqueado = Duration.between(bloqueio.dataBloqueio, bloqueio.dataDesbloqueio).toMinutes();
                    System.out.println("   Data do Desbloqueio: " + bloqueio.dataDesbloqueio.format(formatter));
                    System.out.println("   Motivo do Desbloqueio: " + bloqueio.motivoDesbloqueio);
                    System.out.println("   Tempo Bloqueado: " + formatarTempo(tempoBloqueado));
                } else {
                    System.out.println("   Status: AINDA BLOQUEADO");
                    long tempoBloqueado = Duration.between(bloqueio.dataBloqueio, LocalDateTime.now()).toMinutes();
                    System.out.println("   Tempo Bloqueado até agora: " + formatarTempo(tempoBloqueado));
                }
            }

            // Estatísticas de bloqueios
            gerarEstatisticasBloqueios(bloqueios);

        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório de bloqueios: " + e.getMessage());
        }
    }

    private List<Card> buscarCardsFinalizados(int boardId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT c.* FROM cards c " +
                    "INNER JOIN colunas col ON c.coluna_id = col.id " +
                    "WHERE col.board_id = ? AND col.tipo = 'FINAL' " +
                    "ORDER BY c.data_criacao";

        try (Connection conn = com.boardtarefas.dao.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card card = new Card();
                    card.setId(rs.getInt("id"));
                    card.setTitulo(rs.getString("titulo"));
                    card.setDescricao(rs.getString("descricao"));
                    card.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                    cards.add(card);
                }
            }
        }
        
        return cards;
    }

    private List<MovimentacaoInfo> buscarHistoricoMovimentacao(int cardId) throws SQLException {
        List<MovimentacaoInfo> movimentacoes = new ArrayList<>();
        String sql = "SELECT hm.data_movimentacao, col.nome as nome_coluna " +
                    "FROM historico_movimentacao hm " +
                    "INNER JOIN colunas col ON hm.coluna_destino_id = col.id " +
                    "WHERE hm.card_id = ? " +
                    "ORDER BY hm.data_movimentacao";

        try (Connection conn = com.boardtarefas.dao.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MovimentacaoInfo info = new MovimentacaoInfo();
                    info.dataMovimentacao = rs.getTimestamp("data_movimentacao").toLocalDateTime();
                    info.nomeColuna = rs.getString("nome_coluna");
                    movimentacoes.add(info);
                }
            }
        }
        
        return movimentacoes;
    }

    private List<BloqueioInfo> buscarHistoricoBloqueios(int boardId) throws SQLException {
        List<BloqueioInfo> bloqueios = new ArrayList<>();
        String sql = "SELECT c.titulo, b.motivo_bloqueio, b.data_bloqueio, " +
                    "b.motivo_desbloqueio, b.data_desbloqueio " +
                    "FROM bloqueios b " +
                    "INNER JOIN cards c ON b.card_id = c.id " +
                    "INNER JOIN colunas col ON c.coluna_id = col.id " +
                    "WHERE col.board_id = ? " +
                    "ORDER BY c.titulo, b.data_bloqueio";

        try (Connection conn = com.boardtarefas.dao.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, boardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BloqueioInfo info = new BloqueioInfo();
                    info.tituloCard = rs.getString("titulo");
                    info.motivoBloqueio = rs.getString("motivo_bloqueio");
                    info.dataBloqueio = rs.getTimestamp("data_bloqueio").toLocalDateTime();
                    info.motivoDesbloqueio = rs.getString("motivo_desbloqueio");
                    
                    Timestamp dataDesbloqueio = rs.getTimestamp("data_desbloqueio");
                    if (dataDesbloqueio != null) {
                        info.dataDesbloqueio = dataDesbloqueio.toLocalDateTime();
                    }
                    
                    bloqueios.add(info);
                }
            }
        }
        
        return bloqueios;
    }

    private void gerarEstatisticasGerais(List<Card> cards) {
        if (cards.isEmpty()) return;

        System.out.println("\n📊 ESTATÍSTICAS GERAIS:");
        System.out.println("-".repeat(30));
        System.out.println("Total de cards finalizados: " + cards.size());
        
        // Aqui você pode adicionar mais estatísticas se desejar
    }

    private void gerarEstatisticasBloqueios(List<BloqueioInfo> bloqueios) {
        System.out.println("\n📊 ESTATÍSTICAS DE BLOQUEIOS:");
        System.out.println("-".repeat(30));
        System.out.println("Total de bloqueios: " + bloqueios.size());
        
        long bloqueiosAtivos = bloqueios.stream()
                .filter(b -> b.dataDesbloqueio == null)
                .count();
        
        System.out.println("Bloqueios ainda ativos: " + bloqueiosAtivos);
        System.out.println("Bloqueios resolvidos: " + (bloqueios.size() - bloqueiosAtivos));
    }

    private String formatarTempo(long minutos) {
        if (minutos < 60) {
            return minutos + " minutos";
        } else if (minutos < 1440) { // menos de 24 horas
            long horas = minutos / 60;
            long minutosRestantes = minutos % 60;
            return horas + "h " + minutosRestantes + "min";
        } else {
            long dias = minutos / 1440;
            long horasRestantes = (minutos % 1440) / 60;
            return dias + " dias " + horasRestantes + "h";
        }
    }

    // Classes auxiliares para organizar os dados
    private static class MovimentacaoInfo {
        LocalDateTime dataMovimentacao;
        String nomeColuna;
    }

    private static class BloqueioInfo {
        String tituloCard;
        String motivoBloqueio;
        LocalDateTime dataBloqueio;
        String motivoDesbloqueio;
        LocalDateTime dataDesbloqueio;
    }
}