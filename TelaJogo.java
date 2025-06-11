package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;



//Aluno: Gabriel Pereira de Carvalho 20241370012


/** aqui temos a telajogo improtando da classe JFRAME e incializando a classe externa do jogo da velha principal
 Inciei um vetor de 9 botoes, JLabel para informações de mensagens ao usuário, O JCombox para selecionar os simbolos dos usuarios
O JRadio botton para escolher o modo do jogo e o nivel de dificuldade da máquina e o JPanel para mostrar o nível
 de inteligencia da máquina ( fácil ou dificil )

**/

public class TelaJogo extends JFrame {
    private static final long serialVersionUID = 1L;
    
    
    private JogoDaVelha jogo;
    private JButton[] botoes = new JButton[9];
    private JLabel lblInfo;
    private JComboBox<String> cbSimbolo1, cbSimbolo2;
    private JRadioButton rbJogador, rbMaquina;
    private JRadioButton rbNivelBaixo, rbNivelAlto;
    private JPanel pnlNivel;
    
    public TelaJogo() {
        super("Jogo da Velha");
        configurarJanela();
        inicializarComponentes();
    }
    
    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Painel de configuração
        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new BoxLayout(pnlConfig, BoxLayout.Y_AXIS));
        
        // Seleção de modo de jogo
        JPanel pnlModo = new JPanel();
        ButtonGroup bgModo = new ButtonGroup();
        rbJogador = new JRadioButton("Jogador vs Jogador", true);
        rbMaquina = new JRadioButton("Jogador vs Máquina");
        bgModo.add(rbJogador);
        bgModo.add(rbMaquina);
        pnlModo.add(rbJogador);
        pnlModo.add(rbMaquina);
        
        // Painel de nível (inicialmente invisível)
        pnlNivel = new JPanel();
        ButtonGroup bgNivel = new ButtonGroup();
        rbNivelBaixo = new JRadioButton("Nível Fácil");
        rbNivelAlto = new JRadioButton("Nível Difícil", true);
        bgNivel.add(rbNivelBaixo);
        bgNivel.add(rbNivelAlto);
        pnlNivel.add(rbNivelBaixo);
        pnlNivel.add(rbNivelAlto);
        pnlNivel.setVisible(false);
        
        // Adiciona listeners para mostrar/esconder o painel de nível
        rbJogador.addActionListener(e -> pnlNivel.setVisible(false));
        rbMaquina.addActionListener(e -> pnlNivel.setVisible(true));
        
        // Seleção de símbolos
        JPanel pnlSimbolos = new JPanel();
        cbSimbolo1 = new JComboBox<>(new String[]{"X", "O"});
        cbSimbolo2 = new JComboBox<>(new String[]{"O", "X"});
        pnlSimbolos.add(new JLabel("Jogador 1:"));
        pnlSimbolos.add(cbSimbolo1);
        pnlSimbolos.add(new JLabel("Jogador 2:"));
        pnlSimbolos.add(cbSimbolo2);
        
        // Botão iniciar
        JButton btnIniciar = new JButton("Iniciar Jogo");
        btnIniciar.addActionListener(this::iniciarJogo);
        
        // Adiciona componentes ao painel de configuração
        pnlConfig.add(pnlModo);
        pnlConfig.add(pnlNivel);
        pnlConfig.add(pnlSimbolos);
        pnlConfig.add(btnIniciar);
        
        // Painel do tabuleiro
        JPanel pnlTabuleiro = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            botoes[i] = new JButton();
            botoes[i].setFont(new Font("Arial", Font.BOLD, 40));
            final int pos = i;
            botoes[i].addActionListener(e -> jogar(pos));
            pnlTabuleiro.add(botoes[i]);
        }
        
        // Painel de informações
        lblInfo = new JLabel("Clique em Iniciar Jogo para começar", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Adiciona todos os painéis à janela
        add(pnlConfig, BorderLayout.NORTH);
        add(pnlTabuleiro, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
    }
    
    private void iniciarJogo(ActionEvent e) {
        if (rbJogador.isSelected()) {
            jogo = new JogoDaVelha(
                cbSimbolo1.getSelectedItem().toString(),
                cbSimbolo2.getSelectedItem().toString()
            );
        } else {
            int nivel = rbNivelAlto.isSelected() ? 2 : 1;
            jogo = new JogoDaVelha(
                cbSimbolo1.getSelectedItem().toString(),
                nivel
            );
        }
        
        // Limpa o tabuleiro visual
        for (JButton botao : botoes) {
            botao.setText("");
            botao.setEnabled(true);
        }
        
        atualizarInterface();
        lblInfo.setText("Jogo iniciado. " + 
            (jogo.isContraMaquina() 
                ? "Você (" + jogo.getSimbolo(1) + ") começa!!!" 
                : "Jogador 1 (" + jogo.getSimbolo(1) + ") começa!!!"));
    }
    // aqui a logica é ao clicar em uma jogada do tabuleiro, começar o jogo, ignorando se não iniciou ou terminou
    // também no metodo temos a virada da jogada, atualização do visual pelo atualizar interface e também a lógica do inserimento
    // da maquina e mensagens de erro
    private void jogar(int posicao) {
        if (jogo == null || jogo.terminou()) return;
        
        try {
            int jogadorAtual = jogo.getJogadorAtual();
            jogo.jogaJogador(jogadorAtual, posicao);
            atualizarInterface();
            
            if (!jogo.terminou() && jogo.isContraMaquina()) {
                jogo.jogaMaquina();
                atualizarInterface();
            }
            
            verificarFimJogo();
        } catch (IllegalArgumentException ex) {
            lblInfo.setText(ex.getMessage());
        }
    }
    
    // esse metodo é importante para atualizar os botões com base no estado final do jogo e as mensagens correspondente
    // ao tipo de jogo
    
    private void verificarFimJogo() {
        int resultado = jogo.getResultado();
        if (resultado == 1) {
            lblInfo.setText("Jogador 1 venceu!");
        } else if (resultado == 2) {
            lblInfo.setText(jogo.isContraMaquina() 
                ? "Máquina (" + jogo.getSimbolo(2) + ") venceu!!!!" 
                : "Jogador 2 venceu!!!!");
        } else if (resultado == 0) {
            lblInfo.setText("Empate ");
            
        } else {
        	// Se o jogo ainda não terminou, mostra quem joga agora
            int proximoJogador = jogo.getJogadorAtual();
            String simbolo = jogo.getSimbolo(proximoJogador);
            lblInfo.setText("Vez do " + 
                (jogo.isContraMaquina() && proximoJogador == 2 
                    ? "Máquina (" + simbolo + ")" 
                    : "Jogador " + proximoJogador + " (" + simbolo + ")"));
        }
    }
    
    
    /** esse método ele atualizará os botões com base no estado atual do tabuleiro do jogo da velha, pegando pela matriz com string formada
     * 
    **/
    
    private void atualizarInterface() {
        String[] linhas = jogo.getFoto().split("\n");
        
        for (int i = 0; i < 9; i++) {
            int linha = i / 3;
            int coluna = i % 3;
            String valor = linhas[linha].split("\\|")[coluna].trim();
            botoes[i].setText(valor.isEmpty() ? "" : valor);
            botoes[i].setEnabled(valor.isEmpty() && !jogo.terminou());
        }
    }
    
    
    // aqui esse método principal irá inicializar a aplicação, criando a joanela no Tela e exibindo ela
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaJogo tela = new TelaJogo();
            tela.setVisible(true);
        });
    }
}
