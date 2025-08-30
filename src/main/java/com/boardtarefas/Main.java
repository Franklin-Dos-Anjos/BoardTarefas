package com.boardtarefas;

import com.boardtarefas.util.MenuUtil;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Gerenciamento de Boards...");
        
        MenuUtil menuUtil = new MenuUtil();
        
        try {
            menuUtil.exibirMenuPrincipal();
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado no sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            menuUtil.fecharScanner();
        }
        
        System.out.println("🔚 Sistema finalizado.");
    }
}