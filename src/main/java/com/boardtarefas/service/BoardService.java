package com.boardtarefas.service;

import com.boardtarefas.dao.BoardDAO;
import com.boardtarefas.dao.ColunaDAO;
import com.boardtarefas.dao.CardDAO;
import com.boardtarefas.model.Board;
import com.boardtarefas.model.Coluna;
import com.boardtarefas.model.Card;
import com.boardtarefas.model.TipoColuna;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class BoardService {
    private BoardDAO boardDAO;
    private ColunaDAO colunaDAO;
    private CardDAO cardDAO;
    private Scanner scanner;

    public BoardService() {
        this.boardDAO = new BoardDAO();
        this.colunaDAO = new ColunaDAO();
        this.cardDAO = new CardDAO();
        this.scanner = new Scanner(System.in);
    }

    public void criarNovoBoard() {
        try {
            System.out.print("Digite o nome do board: ");
            String nome = scanner.nextLine().trim();

            if (nome.isEmpty()) {
                System.out.println("Nome do board não pode estar vazio!");
                return;
            }

            if (boardDAO.existeBoard(nome)) {
                System.out.println("Já existe um board com esse nome!");
                return;
            }

            Board board = new Board(nome);
            boardDAO.salvar(board);

            System.out.println("Board criado com sucesso! ID: " + board.getId());
            
            // Criar colunas padrão
            criarColunasIniciais(board.getId());
            
            System.out.println("Colunas padrão criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar board: " + e.getMessage());
        }
    }

    private void criarColunasIniciais(int boardId) throws SQLException {
        // Coluna Inicial (ordem 1)
        Coluna colunaInicial = new Coluna("A Fazer", 1, TipoColuna.INICIAL);
        colunaInicial.setBoardId(boardId);
        colunaDAO.salvar(colunaInicial);

        // Coluna Pendente (ordem 2)
        Coluna colunaPendente = new Coluna("Em Progresso", 2, TipoColuna.PENDENTE);
        colunaPendente.setBoardId(boardId);
        colunaDAO.salvar(colunaPendente);

        // Coluna Final (ordem 3)
        Coluna colunaFinal = new Coluna("Concluído", 3, TipoColuna.FINAL);
        colunaFinal.setBoardId(boardId);
        colunaDAO.salvar(colunaFinal);

        // Coluna Cancelamento (ordem 4)
        Coluna colunaCancelamento = new Coluna("Cancelado", 4, TipoColuna.CANCELAMENTO);
        colunaCancelamento.setBoardId(boardId);
        colunaDAO.salvar(colunaCancelamento);
    }

    public Board selecionarBoard() {
        try {
            List<Board> boards = boardDAO.listarTodos();

            if (boards.isEmpty()) {
                System.out.println("Nenhum board encontrado!");
                return null;
            }

            System.out.println("\n=== BOARDS DISPONÍVEIS ===");
            for (int i = 0; i < boards.size(); i++) {
                System.out.println((i + 1) + ". " + boards.get(i).getNome());
            }

            System.out.print("Selecione um board (número): ");
            int opcao = Integer.parseInt(scanner.nextLine());

            if (opcao < 1 || opcao > boards.size()) {
                System.out.println("Opção inválida!");
                return null;
            }

            Board boardSelecionado = boards.get(opcao - 1);
            carregarDadosCompletos(boardSelecionado);
            
            System.out.println("Board '" + boardSelecionado.getNome() + "' selecionado!");
            return boardSelecionado;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar boards: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }

        return null;
    }

    private void carregarDadosCompletos(Board board) throws SQLException {
        List<Coluna> colunas = colunaDAO.buscarPorBoard(board.getId());
        
        for (Coluna coluna : colunas) {
            List<Card> cards = cardDAO.buscarPorColuna(coluna.getId());
            coluna.setCards(cards);
        }
        
        board.setColunas(colunas);
    }

    public void excluirBoards() {
        try {
            List<Board> boards = boardDAO.listarTodos();

            if (boards.isEmpty()) {
                System.out.println("Nenhum board encontrado!");
                return;
            }

            System.out.println("\n=== BOARDS PARA EXCLUSÃO ===");
            for (int i = 0; i < boards.size(); i++) {
                System.out.println((i + 1) + ". " + boards.get(i).getNome());
            }

            System.out.print("Selecione um board para excluir (número): ");
            int opcao = Integer.parseInt(scanner.nextLine());

            if (opcao < 1 || opcao > boards.size()) {
                System.out.println("Opção inválida!");
                return;
            }

            Board boardParaExcluir = boards.get(opcao - 1);

            System.out.print("Tem certeza que deseja excluir o board '" + 
                           boardParaExcluir.getNome() + "'? (s/n): ");
            String confirmacao = scanner.nextLine().trim().toLowerCase();

            if (confirmacao.equals("s") || confirmacao.equals("sim")) {
                boardDAO.excluir(boardParaExcluir.getId());
                System.out.println("Board excluído com sucesso!");
            } else {
                System.out.println("Exclusão cancelada.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir board: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }
    }

    public void criarCard(Board board) {
        try {
            System.out.print("Digite o título do card: ");
            String titulo = scanner.nextLine().trim();

            if (titulo.isEmpty()) {
                System.out.println("Título do card não pode estar vazio!");
                return;
            }

            System.out.print("Digite a descrição do card: ");
            String descricao = scanner.nextLine().trim();

            // Buscar coluna inicial
            Coluna colunaInicial = colunaDAO.buscarColunaInicial(board.getId());
            if (colunaInicial == null) {
                System.out.println("Erro: Coluna inicial não encontrada!");
                return;
            }

            Card card = new Card(titulo, descricao);
            card.setColunaId(colunaInicial.getId());
            cardDAO.salvar(card);

            // Registrar movimentação inicial
            cardDAO.registrarMovimentacao(card.getId(), 0, colunaInicial.getId());

            System.out.println("Card criado com sucesso na coluna '" + colunaInicial.getNome() + "'!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar card: " + e.getMessage());
        }
    }

    public void moverCard(Board board) {
        try {
            exibirBoard(board);
            
            System.out.print("Digite o ID do card que deseja mover: ");
            int cardId = Integer.parseInt(scanner.nextLine());

            Card card = cardDAO.buscarPorId(cardId);
            if (card == null) {
                System.out.println("Card não encontrado!");
                return;
            }

            if (card.isBloqueado()) {
                System.out.println("Card está bloqueado e não pode ser movido!");
                System.out.println("Motivo do bloqueio: " + card.getMotivoBloqueio());
                return;
            }

            Coluna colunaAtual = colunaDAO.buscarPorId(card.getColunaId());
            if (colunaAtual == null) {
                System.out.println("Erro: Coluna atual não encontrada!");
                return;
            }

            // Verificar se está na coluna final
            if (colunaAtual.getTipo() == TipoColuna.FINAL) {
                System.out.println("Card já está na coluna final!");
                return;
            }

            // Buscar próxima coluna
            Coluna proximaColuna = colunaDAO.buscarProximaColuna(board.getId(), colunaAtual.getOrdemColuna());
            if (proximaColuna == null) {
                System.out.println("Não há próxima coluna disponível!");
                return;
            }

            // Mover card
            int colunaOrigemId = card.getColunaId();
            card.setColunaId(proximaColuna.getId());
            card.setDataEntradaColuna(LocalDateTime.now());
            cardDAO.atualizar(card);

            // Registrar movimentação
            cardDAO.registrarMovimentacao(card.getId(), colunaOrigemId, proximaColuna.getId());

            System.out.println("Card movido de '" + colunaAtual.getNome() + 
                             "' para '" + proximaColuna.getNome() + "' com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao mover card: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }
    }

    public void cancelarCard(Board board) {
        try {
            exibirBoard(board);
            
            System.out.print("Digite o ID do card que deseja cancelar: ");
            int cardId = Integer.parseInt(scanner.nextLine());

            Card card = cardDAO.buscarPorId(cardId);
            if (card == null) {
                System.out.println("Card não encontrado!");
                return;
            }

            Coluna colunaAtual = colunaDAO.buscarPorId(card.getColunaId());
            if (colunaAtual.getTipo() == TipoColuna.FINAL) {
                System.out.println("Não é possível cancelar um card que já foi concluído!");
                return;
            }

            if (colunaAtual.getTipo() == TipoColuna.CANCELAMENTO) {
                System.out.println("Card já está cancelado!");
                return;
            }

            // Buscar coluna de cancelamento
            Coluna colunaCancelamento = colunaDAO.buscarColunaCancelamento(board.getId());
            if (colunaCancelamento == null) {
                System.out.println("Erro: Coluna de cancelamento não encontrada!");
                return;
            }

            // Mover para coluna de cancelamento
            int colunaOrigemId = card.getColunaId();
            card.setColunaId(colunaCancelamento.getId());
            card.setDataEntradaColuna(LocalDateTime.now());
            cardDAO.atualizar(card);

            // Registrar movimentação
            cardDAO.registrarMovimentacao(card.getId(), colunaOrigemId, colunaCancelamento.getId());

            System.out.println("Card cancelado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao cancelar card: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }
    }

    public void bloquearCard(Board board) {
        try {
            exibirBoard(board);
            
            System.out.print("Digite o ID do card que deseja bloquear: ");
            int cardId = Integer.parseInt(scanner.nextLine());

            Card card = cardDAO.buscarPorId(cardId);
            if (card == null) {
                System.out.println("Card não encontrado!");
                return;
            }

            if (card.isBloqueado()) {
                System.out.println("Card já está bloqueado!");
                return;
            }

            System.out.print("Digite o motivo do bloqueio: ");
            String motivo = scanner.nextLine().trim();

            if (motivo.isEmpty()) {
                System.out.println("Motivo do bloqueio não pode estar vazio!");
                return;
            }

            card.bloquear(motivo);
            cardDAO.atualizar(card);
            cardDAO.registrarBloqueio(card.getId(), motivo);

            System.out.println("Card bloqueado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao bloquear card: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }
    }

    public void desbloquearCard(Board board) {
        try {
            exibirBoard(board);
            
            System.out.print("Digite o ID do card que deseja desbloquear: ");
            int cardId = Integer.parseInt(scanner.nextLine());

            Card card = cardDAO.buscarPorId(cardId);
            if (card == null) {
                System.out.println("Card não encontrado!");
                return;
            }

            if (!card.isBloqueado()) {
                System.out.println("Card não está bloqueado!");
                return;
            }

            System.out.print("Digite o motivo do desbloqueio: ");
            String motivo = scanner.nextLine().trim();

            if (motivo.isEmpty()) {
                System.out.println("Motivo do desbloqueio não pode estar vazio!");
                return;
            }

            card.desbloquear(motivo);
            cardDAO.atualizar(card);
            cardDAO.registrarDesbloqueio(card.getId(), motivo);

            System.out.println("Card desbloqueado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao desbloquear card: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
        }
    }

        public void exibirBoard(Board board) {
        try {
            carregarDadosCompletos(board);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("BOARD: " + board.getNome().toUpperCase());
            System.out.println("=".repeat(80));

            for (Coluna coluna : board.getColunas()) {
                System.out.println("\n📋 " + coluna.getNome().toUpperCase() + 
                                 " (" + coluna.getTipo() + ")");
                System.out.println("-".repeat(50));

                if (coluna.getCards().isEmpty()) {
                    System.out.println("   (Nenhum card)");
                } else {
                    for (Card card : coluna.getCards()) {
                        String status = card.isBloqueado() ? " 🔒 BLOQUEADO" : "";
                        System.out.println("   ID: " + card.getId() + " | " + 
                                         card.getTitulo() + status);
                        System.out.println("   Descrição: " + card.getDescricao());
                        if (card.isBloqueado()) {
                            System.out.println("   Motivo do bloqueio: " + card.getMotivoBloqueio());
                        }
                        System.out.println("   Criado em: " + card.getDataCriacao().toString());
                        System.out.println();
                    }
                }
            }
            System.out.println("=".repeat(80));

        } catch (SQLException e) {
            System.err.println("Erro ao exibir board: " + e.getMessage());
        }
    }
}