package com.boardtarefas.util;

import com.boardtarefas.model.Board;
import com.boardtarefas.service.BoardService;
import com.boardtarefas.service.RelatorioService;
import java.util.Scanner;

public class MenuUtil {
    private Scanner scanner;
    private BoardService boardService;
    private RelatorioService relatorioService;

    public MenuUtil() {
        this.scanner = new Scanner(System.in);
        this.boardService = new BoardService();
        this.relatorioService = new RelatorioService();
    }

    public void exibirMenuPrincipal() {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎯 SISTEMA DE GERENCIAMENTO DE BOARDS");
            System.out.println("=".repeat(50));
            System.out.println("1. Criar novo board");
            System.out.println("2. Selecionar board");
            System.out.println("3. Excluir boards");
            System.out.println("4. Sair");
            System.out.println("=".repeat(50));
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        boardService.criarNovoBoard();
                        break;
                    case 2:
                        Board boardSelecionado = boardService.selecionarBoard();
                        if (boardSelecionado != null) {
                            exibirMenuBoard(boardSelecionado);
                        }
                        break;
                    case 3:
                        boardService.excluirBoards();
                        break;
                    case 4:
                        continuar = false;
                        System.out.println("👋 Obrigado por usar o sistema!");
                        break;
                    default:
                        System.out.println("❌ Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }

    public void exibirMenuBoard(Board board) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📋 GERENCIANDO BOARD: " + board.getNome().toUpperCase());
            System.out.println("=".repeat(60));
            System.out.println("1. Visualizar board");
            System.out.println("2. Criar card");
            System.out.println("3. Mover card para próxima coluna");
            System.out.println("4. Cancelar card");
            System.out.println("5. Bloquear card");
            System.out.println("6. Desbloquear card");
            System.out.println("7. Relatório de tempo das tarefas");
            System.out.println("8. Relatório de bloqueios");
            System.out.println("9. Fechar board (voltar ao menu principal)");
            System.out.println("=".repeat(60));
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        boardService.exibirBoard(board);
                        pausar();
                        break;
                    case 2:
                        boardService.criarCard(board);
                        break;
                    case 3:
                        boardService.moverCard(board);
                        break;
                    case 4:
                        boardService.cancelarCard(board);
                        break;
                    case 5:
                        boardService.bloquearCard(board);
                        break;
                    case 6:
                        boardService.desbloquearCard(board);
                        break;
                    case 7:
                        relatorioService.gerarRelatorioTempo(board);
                        pausar();
                        break;
                    case 8:
                        relatorioService.gerarRelatorioBloqueios(board);
                        pausar();
                        break;
                    case 9:
                        continuar = false;
                        System.out.println("📋 Fechando board '" + board.getNome() + "'...");
                        break;
                    default:
                        System.out.println("❌ Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }

    private void pausar() {
        System.out.println("\n⏸️  Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    public void fecharScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}