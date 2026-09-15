/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: AplicacaoTransmissora
* Funcao...........: Iniciar o envio da mensagem pela pilha de camadas
*************************************************************** */
package model;

public class AplicacaoTransmissora {
  /* ***************************************************************
  * Metodo: enviarParaCamadaDeAplicacao
  * Funcao: encaminhar a mensagem para a camada de aplicacao
  * Parametros: mensagem = texto digitado pelo usuario
  * Retorno: void
  *************************************************************** */
  public static void enviarParaCamadaDeAplicacao(String mensagem) {
    CamadaAplicacaoTransmissora.enviarParaCamadaFisica(mensagem);
  }

  /* ***************************************************************
  * Metodo: AplicacaoTransmissora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public void AplicacaoTransmissora() {
    String mensagem = Estado.controlador.obterMensagem();
    Estado.controlador.definirMensagemTransmissor(mensagem);
    enviarParaCamadaDeAplicacao(mensagem);
  }
}
