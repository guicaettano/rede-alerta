package model;
import controller.ControladorPrincipal;
/** Entrada pela GUI no lugar do cin apresentado no slide 4. */
public class AplicacaoTransmissora {
  private final ControladorPrincipal controlador;
  public AplicacaoTransmissora(ControladorPrincipal controlador) { this.controlador = controlador; }
  public void AplicacaoTransmissora() {
    String mensagem = controlador.obterMensagem();
    controlador.definirMensagemTransmissor(mensagem);
    ControladorPrincipal.camadaAplicacaoTransmissora.CamadaDeAplicacaoTransmissora(mensagem);
  }
}
