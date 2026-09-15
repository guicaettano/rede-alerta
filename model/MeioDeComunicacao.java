/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: MeioDeComunicacao
* Funcao...........: Transportar a lista de sinais entre os pontos A e B
*************************************************************** */
package model;

public class MeioDeComunicacao {
  private static volatile Thread transmissao;

  /* ***************************************************************
  * Metodo: transportar
  * Funcao: transferir cada sinal do ponto A para o ponto B
  * Parametros: fluxoBrutoDeBits = lista de sinais A e B
  * Retorno: void
  *************************************************************** */
  public static void transportar(char[] fluxoBrutoDeBits) {
    final char[] fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits.clone();
    final char[] fluxoBrutoDeBitsPontoB = new char[fluxoBrutoDeBitsPontoA.length];
    Estado.controlador.registrarQuantidadeBits(fluxoBrutoDeBitsPontoA.length);

    transmissao = new Thread(() -> {
      try {
        int indice = 0;
        while (indice < fluxoBrutoDeBitsPontoA.length) {
          if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
          fluxoBrutoDeBitsPontoB[indice] = fluxoBrutoDeBitsPontoA[indice];
          char ultimoSinal = indice == 0 ? fluxoBrutoDeBitsPontoB[indice]
              : fluxoBrutoDeBitsPontoB[indice - 1];
          Estado.controlador.atualizaSinal(fluxoBrutoDeBitsPontoB[indice], ultimoSinal);
          indice++;
          Thread.sleep(Estado.controlador.obterAtraso());
        }
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        CamadaFisicaReceptora.enviarParaCamadaDeAplicacao(fluxoBrutoDeBitsPontoB);
      } catch (InterruptedException erro) {
        Thread.currentThread().interrupt();
        Estado.controlador.informarErro("Transmissao cancelada.");
      } catch (RuntimeException erro) {
        Estado.controlador.informarErro("Falha: " + erro.getMessage());
      }
    }, "transmissao");
    transmissao.setDaemon(true);
    transmissao.start();
  }

  /* ***************************************************************
  * Metodo: MeioDeComunicacao
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: fluxoBrutoDeBits = niveis um e zero
  * Retorno: void
  *************************************************************** */
  public void MeioDeComunicacao(int[] fluxoBrutoDeBits) {
    char[] sinais = new char[fluxoBrutoDeBits.length];
    for (int i = 0; i < sinais.length; i++) sinais[i] = fluxoBrutoDeBits[i] == 1 ? 'A' : 'B';
    transportar(sinais);
  }

  /* ***************************************************************
  * Metodo: cancelar
  * Funcao: interromper uma transmissao que esteja em andamento
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public static void cancelar() {
    Thread atual = transmissao;
    if (atual != null) atual.interrupt();
  }
}
