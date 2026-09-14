import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.ControladorPrincipal;

/** Ponto de entrada do simulador Rede Alerta. */
public class Principal extends Application {
  // Faz com que "javac Principal.java" tambem encontre o controller carregado pelo FXML.
  @SuppressWarnings("unused")
  private static final Class<?> CONTROLADOR_PRINCIPAL = ControladorPrincipal.class;

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

  public static void main(String[] argumentos) {
    launch(argumentos);
  }
}
