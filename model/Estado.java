/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: Estado
* Funcao...........: Compartilhar a codificacao e o adaptador da interface
*************************************************************** */
package model;

import controller.ControladorPrincipal;

public class Estado {
  public static TipoDeCodificacaoEnum tipoDeCodificacao = TipoDeCodificacaoEnum.MANCHESTER;
  public static ControladorPrincipal controlador;

  /* ***************************************************************
  * Metodo: Estado
  * Funcao: impedir a criacao de objetos para esta classe global
  * Parametros: nenhum
  * Retorno: objeto Estado
  *************************************************************** */
  private Estado() {
  }
}
