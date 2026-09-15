/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: MeioDeComunicacao
* Funcao...........: Simular a transferencia de bits entre os pontos A e B
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class MeioDeComunicacao {
  private final ControladorPrincipal controlador;
  private volatile Thread transmissao;

  /* ***************************************************************
  * Metodo: MeioDeComunicacao
  * Funcao: associar o meio de comunicacao ao controlador da GUI
  * Parametros: controlador = controlador principal da interface
  * Retorno: objeto MeioDeComunicacao
  *************************************************************** */
  public MeioDeComunicacao(ControladorPrincipal controlador) { this.controlador = controlador; }

  /* ***************************************************************
  * Metodo: MeioDeComunicacao
  * Funcao: transferir cada bit do ponto A para o ponto B
  * Parametros: fluxoBrutoDeBits = niveis produzidos pelo transmissor
  * Retorno: void
  *************************************************************** */
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
  /* ***************************************************************
  * Metodo: cancelar
  * Funcao: interromper uma transmissao que esteja em andamento
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public void cancelar() {
    Thread atual = transmissao;
    if (atual != null) atual.interrupt();
  }
}
