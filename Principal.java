import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import control.ControllerPrincipal;

/** Ponto de entrada do simulador Rede Alerta. */
public class Principal extends Application {
  // Faz com que "javac Principal.java" tambem encontre o controller carregado pelo FXML.
  @SuppressWarnings("unused")
  private static final Class<?> CONTROLADOR_PRINCIPAL = ControllerPrincipal.class;

  @Override
  public void start(Stage palco) throws Exception {
    Parent raiz = FXMLLoader.load(getClass().getResource("/view/view_principal.fxml"));
    Scene cena = new Scene(raiz, 1220, 800);
    palco.setTitle("Rede Alerta \u00b7 Simulador da Camada F\u00edsica");
    palco.setMinWidth(1080);
    palco.setMinHeight(720);
    palco.setScene(cena);
    palco.show();
  }

  public static void main(String[] argumentos) {
    launch(argumentos);
  }
}
