/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: AplicacaoReceptora
* Funcao...........: Exibir na GUI a mensagem recebida
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class AplicacaoReceptora {
  private final ControladorPrincipal controlador;

  /* ***************************************************************
  * Metodo: AplicacaoReceptora
  * Funcao: associar a aplicacao receptora ao controlador da GUI
  * Parametros: controlador = controlador principal da interface
  * Retorno: objeto AplicacaoReceptora
  *************************************************************** */
  public AplicacaoReceptora(ControladorPrincipal controlador) { this.controlador = controlador; }

  /* ***************************************************************
  * Metodo: AplicacaoReceptora
  * Funcao: entregar a mensagem recebida para exibicao na interface
  * Parametros: mensagem = texto reconstruido no destino
  * Retorno: void
  *************************************************************** */
  public void AplicacaoReceptora(String mensagem) {
    controlador.exibirMensagemRecebida(mensagem);
  }
}
