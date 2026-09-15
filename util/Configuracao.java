/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 14/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: Configuracao
* Funcao...........: Centralizar os valores de atraso da transmissao
*************************************************************** */
package util;

public final class Configuracao {
  private static final long ATRASO_MINIMO_MILISSEGUNDOS = 40L;
  private static final long PASSO_ATRASO_MILISSEGUNDOS = 75L;

  /* ***************************************************************
  * Metodo: Configuracao
  * Funcao: impedir a criacao de objetos para esta classe utilitaria
  * Parametros: nenhum
  * Retorno: objeto Configuracao
  *************************************************************** */
  private Configuracao() {
  }

  /* ***************************************************************
  * Metodo: calcularAtraso
  * Funcao: converter a posicao do controle em atraso de transmissao
  * Parametros: valorControle = valor inteiro selecionado no controle
  * Retorno: long com o atraso em milissegundos
  *************************************************************** */
  public static long calcularAtraso(int valorControle) {
    int valorSeguro = Math.max(1, valorControle);
    return ATRASO_MINIMO_MILISSEGUNDOS
        + (valorSeguro - 1) * PASSO_ATRASO_MILISSEGUNDOS;
  }
}
