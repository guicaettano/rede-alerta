package model;
import controller.ControladorPrincipal;
/** Saida pela GUI no lugar do cout apresentado no slide 10. */
public class AplicacaoReceptora {
  private final ControladorPrincipal controlador;
  public AplicacaoReceptora(ControladorPrincipal controlador) { this.controlador = controlador; }
  public void AplicacaoReceptora(String mensagem) {
    controlador.exibirMensagemRecebida(mensagem);
  }
}
