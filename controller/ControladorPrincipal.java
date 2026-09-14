package controller;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import model.AplicacaoReceptora;
import model.AplicacaoTransmissora;
import model.CamadaAplicacaoReceptora;
import model.CamadaAplicacaoTransmissora;
import model.CamadaFisicaReceptora;
import model.CamadaFisicaTransmissora;
import model.MeioDeComunicacao;
import util.Configuracao;

/** Controla somente os componentes visuais do simulador Rede Alerta. */
public class ControladorPrincipal {
  @FXML private ComboBox<String> seletorCodificacao;
  @FXML private TextArea campoMensagem;
  @FXML private Label mensagemTransmitida;
  @FXML private Label mensagemRecebida;
  @FXML private Label etiquetaStatus;
  @FXML private Label valorVelocidade;
  @FXML private Button botaoEnviar;
  @FXML private Slider controleVelocidade;
  @FXML private Canvas canvasOnda;

  private final List<Integer> sinaisVisiveis = new ArrayList<>();
  private int quantidadeBitsTransmitidos;
  private int quantidadeBitsDoFluxo;
  private volatile long atraso = Configuracao.calcularAtraso(7);
  private static volatile int codificacaoAtiva;
  private static ControladorPrincipal instancia;
  private boolean fechado;
  public static int obterCodificacaoAtiva() { return codificacaoAtiva; }
  public long obterAtraso() { return atraso; }
  public String obterMensagem() { return campoMensagem.getText(); }
  public void fechar() {
    fechado = true;
    if (meioDeComunicacao != null) meioDeComunicacao.cancelar();
  }
  public static void registrarQuantidadeBits(int[] quadro, int[] fluxo) {
    instancia.quantidadeBitsDoFluxo = fluxo.length;
  }

  public static AplicacaoTransmissora aplicacaoTransmissora;
  public static CamadaAplicacaoTransmissora camadaAplicacaoTransmissora;
  public static CamadaFisicaTransmissora camadaFisicaTransmissora;
  public static MeioDeComunicacao meioDeComunicacao;
  public static CamadaFisicaReceptora camadaFisicaReceptora;
  public static CamadaAplicacaoReceptora camadaAplicacaoReceptora;
  public static AplicacaoReceptora aplicacaoReceptora;

  @FXML
  private void initialize() {
    instancia = this;
    seletorCodificacao.getItems().setAll(
        "Binaria (NRZ-L)", "Manchester", "Manchester Diferencial");
    seletorCodificacao.getSelectionModel().select(1);
    seletorCodificacao.valueProperty().addListener(
        (observavel, anterior, atual) -> desenharOnda());

    controleVelocidade.valueProperty().addListener((observavel, anterior, atual) -> {
      int valor = atual.intValue();
      atraso = Configuracao.calcularAtraso(valor);
      valorVelocidade.setText(valor <= 3 ? "Rapida" : valor <= 7 ? "Lenta" : "Muito lenta");
    });

    valorVelocidade.setText("Lenta");
    montarCamadasDoFramework();
    desenharOnda();
    campoMensagem.setText("SOS");
  }

  /** Instancia exatamente as sete classes definidas no framework do trabalho. */
  private void montarCamadasDoFramework() {
    aplicacaoTransmissora = new AplicacaoTransmissora(this);
    camadaAplicacaoTransmissora = new CamadaAplicacaoTransmissora();
    camadaFisicaTransmissora = new CamadaFisicaTransmissora();
    meioDeComunicacao = new MeioDeComunicacao(this);
    camadaFisicaReceptora = new CamadaFisicaReceptora();
    camadaAplicacaoReceptora = new CamadaAplicacaoReceptora();
    aplicacaoReceptora = new AplicacaoReceptora(this);
  }

  @FXML
  private void enviar() {
    String mensagem = campoMensagem.getText();
    if (mensagem == null || mensagem.trim().isEmpty()) {
      informarErro("Digite uma mensagem antes de transmitir.");
      return;
    }
    if (mensagem.length() > 180) {
      informarErro("A mensagem pode possuir no maximo 180 caracteres.");
      return;
    }

    codificacaoAtiva = obterCodificacao();
    prepararNovaTransmissao();
    try { aplicacaoTransmissora.AplicacaoTransmissora(); }
    catch (RuntimeException erro) { informarErro("Falha: " + erro.getMessage()); }
  }

  @FXML
  private void carregarExemplo() {
    campoMensagem.setText("ALERTA");
  }

  private void prepararNovaTransmissao() {
    sinaisVisiveis.clear();
    quantidadeBitsTransmitidos = 0;
    quantidadeBitsDoFluxo = 0;
    desenharOnda();
    mensagemRecebida.setText("Aguardando transmissao...");
    mensagemRecebida.getStyleClass().setAll("mensagem-espera");
    botaoEnviar.setDisable(true);
    seletorCodificacao.setDisable(true);
    definirStatus("Transmitindo", "status-transmitindo");
  }

  /** Valor utilizado pelo switch/case da camada fisica do framework. */
  public int obterCodificacao() {
    int indice = seletorCodificacao.getSelectionModel().getSelectedIndex();
    return indice < 0 ? 0 : indice;
  }

  /** Metodo chamado pela AplicacaoTransmissora original. */
  public void definirMensagemTransmissor(String mensagem) {
    mensagemTransmitida.setText(mensagem);

  }

  /** Mantido para compatibilidade com o deslocamento visual do meio original. */
  public void deslocaSinal() {
    // A Canvas redesenha a janela de sinais automaticamente em atualizaSinal.
  }

  /** Recebe cada bit transferido pelo MeioDeComunicacao original. */
  public void atualizaSinal(int bit, int ultimoSinal) {
    Platform.runLater(() -> {
      if (fechado) return;
      sinaisVisiveis.add(bit);
      quantidadeBitsTransmitidos++;
      desenharOnda();
    });
  }

  /** Mantido para compatibilidade com a limpeza final do meio original. */
  public void removeSinal(int indice) {
    // O historico permanece visivel para o usuario inspecionar ao final.
  }

  public void exibirMensagemRecebida(String mensagem) {
    Platform.runLater(() -> {
      if (fechado) return;
      mensagemRecebida.setText(mensagem);
      mensagemRecebida.getStyleClass().setAll("mensagem-valor", "mensagem-recebida");
      botaoEnviar.setDisable(false);
      seletorCodificacao.setDisable(false);
      definirStatus("Canal disponivel", "status-sucesso");
    });
  }

  public void informarErro(String mensagem) {
    Platform.runLater(() -> {
      if (fechado) return;
      etiquetaStatus.setText(mensagem);
      etiquetaStatus.getStyleClass().setAll("status", "status-erro");
      botaoEnviar.setDisable(false);
      seletorCodificacao.setDisable(false);
    });
  }

  private void definirStatus(String texto, String classe) {
    etiquetaStatus.setText(texto);
    etiquetaStatus.getStyleClass().setAll("status", classe);
  }

  private void desenharOnda() {
    GraphicsContext contexto = canvasOnda.getGraphicsContext2D();
    double largura = canvasOnda.getWidth();
    double altura = canvasOnda.getHeight();
    contexto.clearRect(0, 0, largura, altura);

    double origem = 18;
    double destino = largura - 18;
    double centro = altura / 2;
    contexto.setFill(Color.web("#6F8291"));
    contexto.fillText("SAIDA", 17, 24);
    contexto.fillText("CHEGADA", largura - 67, 24);

    contexto.setStroke(Color.web("#C9DFEC"));
    contexto.setLineWidth(2);
    contexto.setLineDashes(6, 8);
    contexto.strokeLine(origem, centro, destino, centro);
    contexto.setLineDashes();

    contexto.setFill(Color.web("#E6F4FE"));
    contexto.fillOval(origem - 12, centro - 12, 24, 24);
    contexto.fillOval(destino - 12, centro - 12, 24, 24);
    contexto.setFill(Color.web("#0697EB"));
    contexto.fillOval(origem - 5, centro - 5, 10, 10);
    contexto.fillOval(destino - 5, centro - 5, 10, 10);

    if (sinaisVisiveis.isEmpty()) {
      contexto.setFill(Color.web("#8496A3"));
      contexto.fillText("O sinal surgira aqui ao iniciar", largura / 2 - 76, altura - 25);
      return;
    }

    int total = Math.max(1, quantidadeBitsDoFluxo);
    double progresso = Math.min(1.0, (double) quantidadeBitsTransmitidos / total);
    double cabeca = origem + (destino - origem) * progresso;
    int maximoVisivel = 30;
    int inicio = Math.max(0, sinaisVisiveis.size() - maximoVisivel);
    int quantidade = sinaisVisiveis.size() - inicio;
    double larguraOnda = Math.min(cabeca - origem, quantidade * 12.0);
    double passo = quantidade == 0 ? 0 : larguraOnda / quantidade;
    double x = cabeca - larguraOnda;

    contexto.setStroke(Color.web("#0697EB"));
    contexto.setLineWidth(3);
    double yAnterior = nivelY(sinaisVisiveis.get(inicio), altura);
    for (int i = inicio; i < sinaisVisiveis.size(); i++) {
      double y = nivelY(sinaisVisiveis.get(i), altura);
      if (i > inicio && y != yAnterior) contexto.strokeLine(x, yAnterior, x, y);
      contexto.strokeLine(x, y, x + passo, y);
      x += passo;
      yAnterior = y;
    }

    contexto.setFill(Color.web("#BCE7FF"));
    contexto.fillOval(cabeca - 11, yAnterior - 11, 22, 22);
    contexto.setFill(Color.web("#0697EB"));
    contexto.fillOval(cabeca - 6, yAnterior - 6, 12, 12);
  }

  private double nivelY(int bit, double altura) {
    return bit == 1 ? altura / 2 - 28 : altura / 2 + 28;
  }
}
