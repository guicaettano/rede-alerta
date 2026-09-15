/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: AplicacaoTransmissora
* Funcao...........: Obter a mensagem e iniciar sua transmissao
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class AplicacaoTransmissora {
  private final ControladorPrincipal controlador;

  /* ***************************************************************
  * Metodo: AplicacaoTransmissora
  * Funcao: associar a aplicacao transmissora ao controlador da GUI
  * Parametros: controlador = controlador principal da interface
  * Retorno: objeto AplicacaoTransmissora
  *************************************************************** */
  public AplicacaoTransmissora(ControladorPrincipal controlador) { this.controlador = controlador; }

  /* ***************************************************************
  * Metodo: AplicacaoTransmissora
  * Funcao: obter a mensagem e chamar a camada de aplicacao transmissora
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public void AplicacaoTransmissora() {
    String mensagem = controlador.obterMensagem();
    controlador.definirMensagemTransmissor(mensagem);
    ControladorPrincipal.camadaAplicacaoTransmissora.CamadaDeAplicacaoTransmissora(mensagem);
  }
}
