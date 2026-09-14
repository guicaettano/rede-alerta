package model;
import controller.ControladorPrincipal;
/** Transferencia de A para B bit a bit, como no slide 7. */
public class MeioDeComunicacao {
  private final ControladorPrincipal controlador;
  private volatile Thread transmissao;
  public MeioDeComunicacao(ControladorPrincipal controlador) { this.controlador = controlador; }
  public void MeioDeComunicacao(int[] fluxoBrutoDeBits) {
    final int[] fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits.clone();
    final int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBitsPontoA.length];
    transmissao = new Thread(() -> {
      try {
        int indice = 0;
        while (indice < fluxoBrutoDeBitsPontoA.length) {
          if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
          fluxoBrutoDeBitsPontoB[indice] = fluxoBrutoDeBitsPontoA[indice];
          controlador.atualizaSinal(fluxoBrutoDeBitsPontoB[indice], -1);
          indice++;
          Thread.sleep(controlador.obterAtraso());
        }
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        ControladorPrincipal.camadaFisicaReceptora.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB);
      } catch (InterruptedException erro) {
        Thread.currentThread().interrupt();
        controlador.informarErro("Transmissao cancelada.");
      } catch (RuntimeException erro) {
        controlador.informarErro("Falha: " + erro.getMessage());
      }
    }, "transmissao");
    transmissao.setDaemon(true);
    transmissao.start();
  }
  public void cancelar() { Thread atual = transmissao; if (atual != null) atual.interrupt(); }
}
