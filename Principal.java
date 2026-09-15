/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: Principal
* Funcao...........: Inicializar a aplicacao JavaFX Rede Alerta
*************************************************************** */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.ControladorPrincipal;

public class Principal extends Application {
  // Faz com que "javac Principal.java" tambem encontre o controller carregado pelo FXML.
  @SuppressWarnings("unused")
  private static final Class<?> CONTROLADOR_PRINCIPAL = ControladorPrincipal.class;

  /* ***************************************************************
  * Metodo: start
  * Funcao: carregar a interface e exibir a janela principal
  * Parametros: palco = janela principal fornecida pelo JavaFX
  * Retorno: void
  *************************************************************** */
  @Override
  public void start(Stage palco) throws Exception {
    FXMLLoader carregador = new FXMLLoader(getClass().getResource("/view/view_principal.fxml"));
    Parent raiz = carregador.load();
    ControladorPrincipal controlador = carregador.getController();
    palco.setOnCloseRequest(evento -> controlador.fechar());
    Scene cena = new Scene(raiz, 1360, 690);
    palco.setTitle("Rede Alerta - Simulador da Camada Fisica");
    palco.setMinWidth(1280);
    palco.setMinHeight(720);
    palco.setScene(cena);
    palco.show();
  }

  /* ***************************************************************
  * Metodo: main
  * Funcao: iniciar o ciclo de vida da aplicacao JavaFX
  * Parametros: argumentos = argumentos recebidos pela linha de comando
  * Retorno: void
  *************************************************************** */
  public static void main(String[] argumentos) {
    launch(argumentos);
  }
}
