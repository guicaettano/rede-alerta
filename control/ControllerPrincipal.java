package control;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.AplicacaoReceptora;
import model.AplicacaoTransmissora;
import model.CamadaAplicacaoReceptora;
import model.CamadaAplicacaoTransmissora;
import model.CamadaFisicaReceptora;
import model.CamadaFisicaTransmissora;
import model.MeioDeComunicacao;

/** Controla somente os componentes visuais do simulador Rede Alerta. */
public class ControllerPrincipal {
  @FXML private ComboBox<String> seletorCodificacao;
  @FXML private TextArea campoMensagem;
  @FXML private TextArea quadroOriginal;
  @FXML private TextArea fluxoCodificado;
  @FXML private Label mensagemTransmitida;
  @FXML private Label mensagemRecebida;
  @FXML private Label etiquetaStatus;
  @FXML private Label progressoTexto;
  @FXML private Label valorVelocidade;
  @FXML private Label detalhesCodificacao;
  @FXML private Button botaoEnviar;
  @FXML private Slider controleVelocidade;
  @FXML private ProgressBar barraProgresso;
  @FXML private Canvas canvasOnda;

  private final List<Integer> sinaisVisiveis = new ArrayList<>();
  private int quantidadeBitsTransmitidos;

  public static AplicacaoTransmissora aT;
  public static CamadaAplicacaoTransmissora cAT;
  public static CamadaFisicaTransmissora cFT;
  public static MeioDeComunicacao mC;
  public static CamadaFisicaReceptora cFR;
  public static CamadaAplicacaoReceptora cAR;
  public static AplicacaoReceptora aR;

  @FXML
  private void initialize() {
    seletorCodificacao.getItems().setAll(
        "Bin\u00e1ria (NRZ-L)", "Manchester", "Manchester Diferencial");
    seletorCodificacao.getSelectionModel().select(1);
    seletorCodificacao.valueProperty().addListener(
        (observavel, anterior, atual) -> atualizarDescricao());

    controleVelocidade.valueProperty().addListener((observavel, anterior, atual) -> {
      int valor = atual.intValue();
      valorVelocidade.setText(valor <= 3 ? "Normal" : valor <= 7 ? "Lenta" : "Muito lenta");
    });

    fluxoCodificado.textProperty().addListener((observavel, anterior, atual) -> {
      progressoTexto.setText("0 / " + atual.length() + " bits");
    });

    montarCamadasDoFramework();
    atualizarDescricao();
    desenharOnda();
    campoMensagem.setText("ALERTA: n\u00edvel do Rio Verruga acima da cota de seguran\u00e7a.");
  }

  /** Instancia exatamente as sete classes definidas no framework do trabalho. */
  private void montarCamadasDoFramework() {
    aT = new AplicacaoTransmissora(this);
    cAT = new CamadaAplicacaoTransmissora(this);
    cFT = new CamadaFisicaTransmissora(this, fluxoCodificado);

    ImageView botaoVoltarDaInterfaceOriginal = new ImageView();
    mC = new MeioDeComunicacao(this, controleVelocidade, botaoVoltarDaInterfaceOriginal);
    cFR = new CamadaFisicaReceptora(this);
    cAR = new CamadaAplicacaoReceptora(this);

    Text textoDaInterfaceOriginal = new Text();
    ImageView botaoEnviarDaInterfaceOriginal = new ImageView();
    ImageView fundoDaInterfaceOriginal = new ImageView();
    aR = new AplicacaoReceptora(
        this,
        textoDaInterfaceOriginal,
        botaoEnviarDaInterfaceOriginal,
        botaoVoltarDaInterfaceOriginal,
        fundoDaInterfaceOriginal) {
      @Override
      public void aplicacaoReceptora(String mensagem) {
        exibirMensagemRecebida(mensagem);
      }
    };
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

    prepararNovaTransmissao();
    aT.aplicacaoTransmissora(mensagem);
  }

  @FXML
  private void carregarExemplo() {
    campoMensagem.setText("EVACUA\u00c7\u00c3O PREVENTIVA: dirija-se ao abrigo do bairro imediatamente.");
  }

  private void prepararNovaTransmissao() {
    sinaisVisiveis.clear();
    quantidadeBitsTransmitidos = 0;
    desenharOnda();
    mensagemRecebida.setText("Aguardando transmiss\u00e3o...");
    mensagemRecebida.getStyleClass().setAll("message-placeholder");
    quadroOriginal.clear();
    fluxoCodificado.clear();
    barraProgresso.setProgress(0);
    progressoTexto.setText("0 / 0 bits");
    botaoEnviar.setDisable(true);
    seletorCodificacao.setDisable(true);
    definirStatus("Transmitindo", "status-active");
  }

  /** Valor utilizado pelo switch/case da camada fisica do framework. */
  public int getCodificacao() {
    int indice = seletorCodificacao.getSelectionModel().getSelectedIndex();
    return indice < 0 ? 0 : indice;
  }

  /** Metodo chamado pela AplicacaoTransmissora original. */
  public void setMensagemTransmissor(String mensagem) {
    mensagemTransmitida.setText(mensagem);
    quadroOriginal.setText(converterMensagemParaVisualizacaoBinaria(mensagem));
  }

  /** Mantido para compatibilidade com o deslocamento visual do meio original. */
  public void deslocaSinal() {
    // A Canvas redesenha a janela de sinais automaticamente em atualizaSinal.
  }

  /** Recebe cada bit transferido pelo MeioDeComunicacao original. */
  public void atualizaSinal(int bit, int ultimoSinal) {
    Platform.runLater(() -> {
      sinaisVisiveis.add(bit);
      quantidadeBitsTransmitidos++;
      desenharOnda();
      int total = Math.max(1, fluxoCodificado.getText().length());
      barraProgresso.setProgress(Math.min(1.0, (double) quantidadeBitsTransmitidos / total));
      progressoTexto.setText(quantidadeBitsTransmitidos + " / " + total + " bits");
    });
  }

  /** Mantido para compatibilidade com a limpeza final do meio original. */
  public void removeSinal(int indice) {
    // O historico permanece visivel para o usuario inspecionar ao final.
  }

  private void exibirMensagemRecebida(String mensagem) {
    Platform.runLater(() -> {
      mensagemRecebida.setText(mensagem);
      mensagemRecebida.getStyleClass().setAll("message-value", "received-value");
      botaoEnviar.setDisable(false);
      seletorCodificacao.setDisable(false);
      barraProgresso.setProgress(1);
      definirStatus("Canal dispon\u00edvel", "status-success");
    });
  }

  private void informarErro(String mensagem) {
    etiquetaStatus.setText(mensagem);
    etiquetaStatus.getStyleClass().setAll("status-pill", "status-error");
  }

  private void atualizarDescricao() {
    switch (getCodificacao()) {
      case 0:
        detalhesCodificacao.setText("NRZ-L \u00b7 0 = baixo \u00b7 1 = alto");
        break;
      case 1:
        detalhesCodificacao.setText("0 = baixo\u2192alto \u00b7 1 = alto\u2192baixo");
        break;
      case 2:
        detalhesCodificacao.setText("Manchester diferencial conforme o framework");
        break;
      default:
        detalhesCodificacao.setText("");
    }
  }

  private String converterMensagemParaVisualizacaoBinaria(String mensagem) {
    StringBuilder binario = new StringBuilder();
    int mascara = 1 << 7;
    for (char caractere : mensagem.toCharArray()) {
      int valor = caractere;
      for (int i = 0; i < 8; i++) {
        binario.append((valor & mascara) == 0 ? 0 : 1);
        valor <<= 1;
      }
      binario.append(' ');
    }
    return binario.toString().trim();
  }

  private void definirStatus(String texto, String classe) {
    etiquetaStatus.setText(texto);
    etiquetaStatus.getStyleClass().setAll("status-pill", classe);
  }

  private void desenharOnda() {
    GraphicsContext contexto = canvasOnda.getGraphicsContext2D();
    double largura = canvasOnda.getWidth();
    double altura = canvasOnda.getHeight();
    contexto.clearRect(0, 0, largura, altura);

    contexto.setStroke(Color.web("#D7E5E1"));
    contexto.setLineWidth(1);
    contexto.setLineDashes(4, 7);
    contexto.strokeLine(0, 42, largura, 42);
    contexto.strokeLine(0, altura - 42, largura, altura - 42);

    contexto.setFill(Color.web("#8A9A96"));
    contexto.fillText("1", 8, 35);
    contexto.fillText("0", 8, altura - 30);

    if (sinaisVisiveis.isEmpty()) {
      contexto.fillText("O sinal aparecer\u00e1 durante a transmiss\u00e3o", 88, altura / 2 + 4);
      return;
    }

    int maximoVisivel = 36;
    int inicio = Math.max(0, sinaisVisiveis.size() - maximoVisivel);
    int quantidade = sinaisVisiveis.size() - inicio;
    double margem = 28;
    double passo = (largura - margem - 8) / Math.max(quantidade, maximoVisivel);

    contexto.setStroke(Color.web("#0697EB"));
    contexto.setLineWidth(3);
    contexto.setLineDashes();

    double x = margem;
    double yAnterior = nivelY(sinaisVisiveis.get(inicio), altura);
    for (int i = inicio; i < sinaisVisiveis.size(); i++) {
      double y = nivelY(sinaisVisiveis.get(i), altura);
      if (i > inicio && y != yAnterior) {
        contexto.strokeLine(x, yAnterior, x, y);
      }
      contexto.strokeLine(x, y, x + passo, y);
      x += passo;
      yAnterior = y;
    }
  }

  private double nivelY(int bit, double altura) {
    return bit == 1 ? 42 : altura - 42;
  }
}
