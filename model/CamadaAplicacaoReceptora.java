/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaAplicacaoReceptora
* Funcao...........: Reconstruir a mensagem recebida no quadro
*************************************************************** */
package model;

public class CamadaAplicacaoReceptora {
  /* ***************************************************************
  * Metodo: enviarParaAplicacao
  * Funcao: decodificar o quadro e entregar a mensagem a aplicacao
  * Parametros: quadro = inteiros recebidos da camada fisica
  * Retorno: void
  *************************************************************** */
  public static void enviarParaAplicacao(int[] quadro) {
    String mensagem = decodificarArrayInt(quadro);
    AplicacaoReceptora.exibir(mensagem);
  }

  /* ***************************************************************
  * Metodo: decodificarArrayInt
  * Funcao: retirar quatro caracteres de oito bits de cada int
  * Parametros: quadro = inteiros que armazenam os caracteres
  * Retorno: String com a mensagem reconstruida
  *************************************************************** */
  private static String decodificarArrayInt(int[] quadro) {
    String mensagem = "";
    int mascara = -16777216;

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 4; j++) {
        mensagem += (char) ((quadro[i] & mascara) >> 24);
        quadro[i] <<= 8;
      }
    }
    return mensagem;
  }

  /* ***************************************************************
  * Metodo: CamadaDeAplicacaoReceptora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: quadro = inteiros recebidos da camada fisica
  * Retorno: void
  *************************************************************** */
  public void CamadaDeAplicacaoReceptora(int[] quadro) {
    enviarParaAplicacao(quadro);
  }
}
