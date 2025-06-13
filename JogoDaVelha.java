package Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Aluno: Gabriel Pereira de Carvalho 20241370012

/* aqui é a classe que implementa toda a lógica principal do jogo da velha */
public class JogoDaVelha {
    // variações possíveis de combinações para vencer o jogo em formato de dicionário
    private final String[] ResultadoVencedor = {"012", "345", "678", "036", "147", "258", "048", "246"};
    private String[] celulas; // aqui representa as noves posições do tabuleiro
    private String[] simbolos; // aqui vai representar os símbolos das jogada que irá aparecer no tabuleiro [ do jogador 1, e do jogador 2/máquina
    private LinkedHashMap<Integer, String> historico; // histórico de jogadas
    private int qtdJogadas; // inicializador e o contador de jogadas feitas
    private int nivelIa; // Nível de inteligência da máquina (1=fácil, 2=difícil)
    private boolean contraMaquina; // lógica booleana para indicar se é jogador ou máquina
    
    /* construtor do modo jogador vs jogador (sem máquina) */
    public JogoDaVelha(String simbolo1, String simbolo2) {
        // Validações adicionadas
    	// primeiro verifcica se são null, segundo verifica se os simbolos são iguais
    	// e terceiro a condição é atingir que cada símbolo seja um único caractere
    	// se a exeção for lançada o throw aparece
        if (simbolo1 == null || simbolo2 == null) {
            throw new IllegalArgumentException("Símbolos não podem ser nulos");
        }
        if (simbolo1.equals(simbolo2)) {
            throw new IllegalArgumentException("Os símbolos devem ser diferentes");
        }
        if (simbolo1.length() != 1 || simbolo2.length() != 1) {
            throw new IllegalArgumentException("Símbolos devem ser caracteres únicos");
        }
        
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.historico = new LinkedHashMap<>();
        this.qtdJogadas = 0;
        this.contraMaquina = false;
        
        this.simbolos[0] = simbolo1;
        this.simbolos[1] = simbolo2; // lógica implementada para garantir que os jogadores passem pro construtor de maneira correta
    }

    // construtor de jogador vs maquina, o qual dessa vez irá aparecer o nivel de jogada para selecionar (1 para burra/fácil 2 para esperteza/dificil)
    public JogoDaVelha(String simboloJogador, int nivel) {
        // Validações, o qual segue a mesma logica do comentário anterior
        if (simboloJogador == null) {
            throw new IllegalArgumentException("Símbolo do jogador não pode ser nulo");
        }
        if (simboloJogador.length() != 1) {
            throw new IllegalArgumentException("Símbolo deve ser um caractere único");
        }
        if (nivel < 1 || nivel > 2) {
            throw new IllegalArgumentException("Nível deve ser 1 (fácil) ou 2 (difícil)");
        }
        
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.historico = new LinkedHashMap<>();
        this.qtdJogadas = 0;
        this.contraMaquina = true;
        this.nivelIa = nivel;
        
        this.simbolos[0] = simboloJogador;
        this.simbolos[1] = "M"; // aqui se representa maquina
    }
    
    // aqui será uma lógica booleana o qual irá retornar se é contra outro jogador ou contra outra maquina
    public boolean isContraMaquina() {
        return contraMaquina;
    }
    
    // o valor par é a vez do jogador 1, enquanto o valor impar é a vez do jogador 2 ou a maquina msm
    public int getJogadorAtual() {
        return (qtdJogadas % 2 == 0) ? 1 : 2;
    }
    
    // aqui dps que se for escolhido a maquina, temos a opção de escolher entre a jogada de nivel de dificuldade 1 ou 2 
    public void jogaMaquina() {
        if (nivelIa == 1) {
            jogadaAleatoria();
        } else {
            jogadaEstrategica();
        }
    }
    
    // aqui é a logica da jogada burra, o qual utilizamos um array e pegamos as posições livres e escolhemos de acordo com o random 
    // ou seja, vai pegar uma lista de posições livre, e ele irá gerar uma posição entre 0 e 1, vai multiplicar pelo numero
    // de posições livre, e irá pegar a parte inteira e pegar da lista
    private void jogadaAleatoria() {
        ArrayList<Integer> posicoesLivres = getPosicoesDisponiveis();
        if (!posicoesLivres.isEmpty()) {
            int posicao = posicoesLivres.get((int)(Math.random() * posicoesLivres.size()));
            jogaJogador(2, posicao);
        }
    }
    
    /** a maquina inteligente aqui, primeiro tentamos completar uma linha para a vitória, caso não consiga, tentará bloquear o jogador 1, se falhar
     * tentará ocupar o centro, e se não dê certo, a maquina vai optar pela jogada aleatoria, resumindo
     *  a jogada estratégica da máquina: tenta ganhar, bloquear, pegar o centro, ou fazer jogada aleatória 
    **/
    // ou seja, vai verificar se tem uma posição vazia de dois simbolos M iguais e ele tenta jogar
    // A pisição de bloquear, se o jogador 1 tiver 2 sequencias de simbolos X iguais, ele tenta bloquear colocando M la
    
    // ou seja, se eu n jogar no centro, ele vai primeiro la, se eu nao tiver 2 jogadas seguidas em uma linha,
    // ele irá jogar aleatoriamente,se eu tiver 2 seguidas, ele me bloqueia
    
    private void jogadaEstrategica() {
    	
    	 if (posicao == -1 && celulas[4] == null) {
             posicao = 4; // Tenta pegar o centro primeiramente
         }
    	 
        int posicao = getJogadaVencedora(2); // Tenta vencer dps de duas jogadas seguida da maquina
        
        if (posicao == -1) {
            posicao = getJogadaVencedora(1); // Tenta bloquear as 2 jogadas do jogador
        }
        
       
        
        if (posicao == -1) {
            jogadaAleatoria(); // Jogada aleatória se não encontrou estratégia
        } else {
            jogaJogador(2, posicao);
        }
    }
    
    // aqui o metodo de retornar  o símbolo dele com base no seu número
    public String getSimbolo(int numeroJogador) {
        return simbolos[numeroJogador - 1];
    }
    
    // método que verifica o resultado do jogo: 1 = jogador 1 venceu, 2 = jogador 2 ou máquina venceu, 0 = empate, -1 = jogo em andamento
    public int getResultado() {
        if (qtdJogadas < 3) {
            return -1;
        }
        
        for (String resultado : ResultadoVencedor) { // vai percorrer o array do resultado vencendor, e vai verificar cada indice do dicionario
        	// ele vai convertecer cada caractere da string em numero
            int indice1 = Character.getNumericValue(resultado.charAt(0));
            int indice2 = Character.getNumericValue(resultado.charAt(1));
            int indice3 = Character.getNumericValue(resultado.charAt(2));
            
            // ali em cima vai pegar 3 posiões 
            
            
            
            String check1 = celulas[indice1];
            if (check1 == null) continue;
            String check2 = celulas[indice2];
            String check3 = celulas[indice3];
            
            // aqui vai pegar o simbolo que ta sendo verificado em cada caractere, e se tiver vazia procura o prox
            
            // vai verificar as posiç~´oes, porém ele vai retornar o simbolo do jogador, se foi 1 ou 2 e decidir a vitoria
            
            if (check1.equals(check2) && check2.equals(check3)) {
                return check1.equals(getSimbolo(1)) ? 1 : 2;
            }
        }
        // aqui é qnd ninguem ganhou e gerou um empate
        return qtdJogadas == 9 ? 0 : -1;
    }
    
    // aqui vai retornar uma lista com todas as posições ainda disponíveis no tabuleiro do jogo da velha
    // esse vai ser util para a ia descobrir qual posição ta vazia e jogar
    public ArrayList<Integer> getPosicoesDisponiveis() {
        ArrayList<Integer> posicoesLivres = new ArrayList<>();
        for (int i = 0; i < celulas.length; i++) {
            if (celulas[i] == null) {
                posicoesLivres.add(i);
            }
        }
        return posicoesLivres;
    }
    
    // verificamos se terminou ou nao ( vitoria ou empate ) vai fazer isso retornando 1 ou 2, se for diferente é pq ta em andamento ( false )
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
    
    // a logica aqui é passado pela máquina para verificar se existe uma jogada vencedora ou de bloqueio
    // ele vai pegar o simbolo do jogador atual
    private int getJogadaVencedora(int numeroJogador) {
        String simbolo = getSimbolo(numeroJogador);
        
        for (String resultado : ResultadoVencedor) { // aqui vai analisar as possiveis linhas vencedoras ( 3 h, 3 v e 2 diagonais
            int[] posicoes = {
                Character.getNumericValue(resultado.charAt(0)),
                Character.getNumericValue(resultado.charAt(1)),
                Character.getNumericValue(resultado.charAt(2))
            };
            // aqui em cima ele vai converter a combinaçao em posições numericas!
            
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
    
    // formato do tabuleiro em string em estado atual,  o qual ele vai formatar e mostrar organizada
    public String getFoto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(celulas[i] == null ? " " : celulas[i]);
            if (i % 3 == 2 && i < 8) sb.append("\n");
            if (i % 3 != 2) sb.append("|");
        }
        return sb.toString();
    }
    
    // aqui irá retornar a quantidade de jogadas realizadas até o momento
    public int getQuantidadeJogadas() {
        return qtdJogadas;
    }
    
    // Método getHistorico ajustado para retornar List<Map> conforme especificação 
    /** 
     * esse metodo vai transformar o historico de jogada em varios mapas o qual cada elemtno
     *  vai representar uma jogada especifica do simbolo e da posição, e mantém a ordem de jogarda
     *   corretamente qnd for fazer a exibição ou analise do histórico assim que terminar a partida**/
    public List<Map<Integer, String>> getHistorico() {
        List<Map<Integer, String>> listaHistorico = new ArrayList<>();
        
        // Converte cada entrada do LinkedHashMap em um Map individual
        for (Map.Entry<Integer, String> entry : historico.entrySet()) {
            Map<Integer, String> jogada = new LinkedHashMap<>();
            jogada.put(entry.getKey(), entry.getValue());
            listaHistorico.add(jogada);
        }
        
        return listaHistorico;
    }
}
