package Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/* aqui é a classe que implemeta toda a lógica principal do jogo da velha */


public class JogoDaVelha {
	// variações possíveis de combinações para vencer o jogo em formato de dicionário
    private final String[] ResultadoVencedor = {"012", "345", "678", "036", "147", "258", "048", "246"};
    private String[] celulas; // aqui representa as noves posições do tabuleiro
    private String[] simbolos; // aqui vai representar os símbolos das jogada que irá aparecer no tabuleiro [ do jogador 1, e do jogador 2/máquina
    private LinkedHashMap<Integer, String> historico; // histórico de jogadas
    private int qtdJogadas; // inicializador e o contador de jogadas feitas
    private int nivelIa; // Nível de inteligência da máquina (1=fácil, 2=difícil)
    private boolean contraMaquina; // lógica boleana para indicar se é jogador ou máquina
    
    
    /* construtor do modo jogador vs jogador ( sem máquina */
    
    public JogoDaVelha(String simbolo1, String simbolo2) {
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.historico = new LinkedHashMap<>();
        this.qtdJogadas = 0;
        this.contraMaquina = false;
        
        this.simbolos[0] = simbolo1;
        this.simbolos[1] = simbolo2; // lógica implementada para garantir que os jogadores passem pro construtor de maneira correta
    }
    
    // construtor de jogador vs maquina, o qual dessa vez irá aparecer o nivel de jogada para selecionar ( 1 para burra/fácil 2 para esperteza/dificil )

    public JogoDaVelha(String simboloJogador, int nivel) {
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.historico = new LinkedHashMap<>();
        this.qtdJogadas = 0;
        this.contraMaquina = true;
        this.nivelIa = nivel;
        
        this.simbolos[0] = simboloJogador;
        this.simbolos[1] = "M"; // aqui se representa maquina
    }
    
    public boolean isContraMaquina() {
        return contraMaquina;
    }
    
    public int getJogadorAtual() {
        return (qtdJogadas % 2 == 0) ? 1 : 2;
    }
    
    public void jogaMaquina() {
        if (nivelIa == 1) {
            jogadaAleatoria();
        } else {
            jogadaEstrategica();
        }
    }
    
    private void jogadaAleatoria() {
        ArrayList<Integer> posicoesLivres = getPosicoesDisponiveis();
        if (!posicoesLivres.isEmpty()) {
            int posicao = posicoesLivres.get((int)(Math.random() * posicoesLivres.size()));
            jogaJogador(2, posicao);
        }
    }
    
    private void jogadaEstrategica() {
        int posicao = getJogadaVencedora(2); // Tenta vencer
        
        if (posicao == -1) {
            posicao = getJogadaVencedora(1); // Tenta bloquear
        }
        
        if (posicao == -1 && celulas[4] == null) {
            posicao = 4; // Centro
        }
        
        if (posicao == -1) {
            jogadaAleatoria(); // Jogada aleatória se não encontrou estratégia
        } else {
            jogaJogador(2, posicao);
        }
    }
    
    public String getSimbolo(int numeroJogador) {
        return simbolos[numeroJogador - 1];
    }
    
    public int getResultado() {
        if (qtdJogadas < 3) {
            return -1;
        }
        
        for (String resultado : ResultadoVencedor) {
            int indice1 = Character.getNumericValue(resultado.charAt(0));
            int indice2 = Character.getNumericValue(resultado.charAt(1));
            int indice3 = Character.getNumericValue(resultado.charAt(2));
            
            String check1 = celulas[indice1];
            if (check1 == null) continue;
            String check2 = celulas[indice2];
            String check3 = celulas[indice3];
            
            if (check1.equals(check2) && check2.equals(check3)) {
                return check1.equals(getSimbolo(1)) ? 1 : 2;
            }
        }
        
        return qtdJogadas == 9 ? 0 : -1;
    }
    
    public ArrayList<Integer> getPosicoesDisponiveis() {
        ArrayList<Integer> posicoesLivres = new ArrayList<>();
        for (int i = 0; i < celulas.length; i++) {
            if (celulas[i] == null) {
                posicoesLivres.add(i);
            }
        }
        return posicoesLivres;
    }
    
    public boolean terminou() {
        return getResultado() != -1;
    }
    
    public void jogaJogador(int numeroJogador, int posicao) {
        if (posicao < 0 || posicao >= celulas.length) {
            throw new IllegalArgumentException("Posição inválida: " + posicao);
        }
        if (numeroJogador < 1 || numeroJogador > 2) {
            throw new IllegalArgumentException("Jogador inválido: " + numeroJogador);
        }
        if (celulas[posicao] != null) {
            throw new IllegalArgumentException("Posição já ocupada");
        }
        
        celulas[posicao] = getSimbolo(numeroJogador);
        qtdJogadas++;
        historico.put(posicao, getSimbolo(numeroJogador));
    }
    
    private int getJogadaVencedora(int numeroJogador) {
        String simbolo = getSimbolo(numeroJogador);
        
        for (String resultado : ResultadoVencedor) {
            int[] posicoes = {
                Character.getNumericValue(resultado.charAt(0)),
                Character.getNumericValue(resultado.charAt(1)),
                Character.getNumericValue(resultado.charAt(2))
            };
            
            int vazias = 0;
            int iguais = 0;
            int posicaoVazia = -1;
            
            for (int pos : posicoes) {
                if (celulas[pos] == null) {
                    vazias++;
                    posicaoVazia = pos;
                } else if (celulas[pos].equals(simbolo)) {
                    iguais++;
                }
            }
            
            if (vazias == 1 && iguais == 2) {
                return posicaoVazia;
            }
        }
        return -1;
    }
    
    public String getFoto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(celulas[i] == null ? " " : celulas[i]);
            if (i % 3 == 2 && i < 8) sb.append("\n");
            if (i % 3 != 2) sb.append("|");
        }
        return sb.toString();
    }
    
    public int getQuantidadeJogadas() {
        return qtdJogadas;
    }
}