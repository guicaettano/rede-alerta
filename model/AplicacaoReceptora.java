/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: AplicacaoReceptora
* Funcao...........: Entregar a mensagem recebida para a interface
*************************************************************** */
package model;

public class AplicacaoReceptora {
  /* ***************************************************************
  * Metodo: exibir
  * Funcao: remover o preenchimento e exibir a mensagem no destino
  * Parametros: mensagem = texto reconstruido pela camada de aplicacao
  * Retorno: void
  *************************************************************** */
  public static void exibir(String mensagem) {
    int fim = mensagem.indexOf(0);
    if (fim >= 0) mensagem = mensagem.substring(0, fim);
    Estado.controlador.exibirMensagemRecebida(mensagem);
  }

  /* ***************************************************************
  * Metodo: AplicacaoReceptora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: mensagem = texto reconstruido no destino
  * Retorno: void
  *************************************************************** */
  public void AplicacaoReceptora(String mensagem) {
    exibir(mensagem);
  }
}
