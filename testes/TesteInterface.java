/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 14/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: TesteInterface
* Funcao...........: Validar a interface e as transmissoes completas
*************************************************************** */
package testes;

import controller.ControladorPrincipal;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import javax.imageio.ImageIO;

public class TesteInterface extends Application {
  private Parent raiz;
  private Stage palco;
  private ControladorPrincipal controlador;
  private int opcao;
  /* ***************************************************************
  * Metodo: exigir
  * Funcao: interromper o teste quando a interface estiver incorreta
  * Parametros: condicao = resultado logico que deve ser verdadeiro
  * Retorno: void
  *************************************************************** */
  private void exigir(boolean condicao) {
    if (!condicao) throw new AssertionError("Falha na interface, opcao " + opcao);
  }
  /* ***************************************************************
  * Metodo: start
  * Funcao: carregar a GUI e iniciar a sequencia de testes
  * Parametros: janela = palco JavaFX usado durante a verificacao
  * Retorno: void
  *************************************************************** */
  @Override public void start(Stage janela) throws Exception {
    palco = janela;
    FXMLLoader carregador = new FXMLLoader(getClass().getResource("/view/view_principal.fxml"));
    raiz = carregador.load();
    controlador = carregador.getController();
    palco.setScene(new Scene(raiz, 1360, 690));
    palco.show();
    raiz.applyCss();
    raiz.layout();
    ((Slider) raiz.lookup("#controleVelocidade")).setValue(1);
    transmitir();
  }
  /* ***************************************************************
  * Metodo: salvarCaptura
  * Funcao: registrar uma imagem da interface durante a transmissao
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  private void salvarCaptura() {
    try {
      WritableImage imagem = new WritableImage(1360, 690);
      raiz.snapshot(null, imagem);
      ImageIO.write(SwingFXUtils.fromFXImage(imagem, null), "png",
          new File("img/preview-rede-alerta.png"));
    } catch (Exception erro) { throw new RuntimeException(erro); }
  }
  /* ***************************************************************
  * Metodo: transmitir
  * Funcao: testar sequencialmente cada opcao de codificacao
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  private void transmitir() {
    ((ComboBox<?>) raiz.lookup("#seletorCodificacao")).getSelectionModel().select(opcao);
    ((TextArea) raiz.lookup("#campoMensagem")).setText("SOS");
    ((Button) raiz.lookup("#botaoEnviar")).fire();
    exigir(raiz.lookup("#botaoEnviar").isDisabled());
    PauseTransition espera = new PauseTransition(Duration.seconds(8));
    espera.setOnFinished(evento -> {
      try {
        exigir(((Label) raiz.lookup("#mensagemRecebida")).getText().equals("SOS"));
        exigir(!raiz.lookup("#botaoEnviar").isDisabled());
        exigir(raiz.lookup("#barraProgresso") == null);
        exigir(raiz.lookup("#fluxoCodificado") == null);
        if (++opcao < 3) {
          transmitir();
          if (opcao == 1) {
            PauseTransition captura = new PauseTransition(Duration.millis(600));
            captura.setOnFinished(e -> salvarCaptura());
            captura.play();
          }
          return;
        }
        ((TextArea) raiz.lookup("#campoMensagem")).setText("Teste de cancelamento");
        ((Button) raiz.lookup("#botaoEnviar")).fire();
        controlador.fechar();
        PauseTransition fim = new PauseTransition(Duration.millis(300));
        fim.setOnFinished(e -> {
          for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals("transmissao") && thread.isAlive()) {
              System.err.println("Thread de transmissao nao terminou");
              System.exit(1);
            }
          }
          System.out.println("OK: FXML, tres transmissoes, controles e cancelamento.");
          palco.close();
          Platform.exit();
        });
        fim.play();
      } catch (Throwable erro) { erro.printStackTrace(); System.exit(1); }
    });
    espera.play();
  }
  /* ***************************************************************
  * Metodo: main
  * Funcao: iniciar o teste automatizado da interface JavaFX
  * Parametros: argumentos = argumentos recebidos pela linha de comando
  * Retorno: void
  *************************************************************** */
  public static void main(String[] argumentos) {
    launch(argumentos);
  }
}
