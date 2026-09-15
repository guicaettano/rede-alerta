/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaAplicacaoTransmissora
* Funcao...........: Converter a mensagem em um quadro de inteiros
*************************************************************** */
package model;

public class CamadaAplicacaoTransmissora {
  /* ***************************************************************
  * Metodo: enviarParaCamadaFisica
  * Funcao: codificar a mensagem e encaminhar o quadro para a rede
  * Parametros: mensagem = texto informado pelo usuario
  * Retorno: void
  *************************************************************** */
  public static void enviarParaCamadaFisica(String mensagem) {
    if (!mensagem.isEmpty()) {
      int[] quadro = codificarEmArrayInt(mensagem);
      CamadaFisicaTransmissora.enviarPelaRede(quadro);
    }
  }

  /* ***************************************************************
  * Metodo: codificarEmArrayInt
  * Funcao: empacotar ate quatro caracteres de oito bits em cada int
  * Parametros: mensagem = texto que sera empacotado
  * Retorno: int[] com o quadro da camada de aplicacao
  *************************************************************** */
  private static int[] codificarEmArrayInt(String mensagem) {
    int[] quadro = new int[(int) Math.ceil(mensagem.length() / 4.0)];
    int indiceQuadro = 0;

    for (int i = 1; i <= mensagem.length(); i++) {
      quadro[indiceQuadro] |= mensagem.charAt(i - 1);
      if (i % 4 != 0) quadro[indiceQuadro] <<= 8;
      else indiceQuadro++;
    }

    int espacosLivres = mensagem.length() % 4 == 0 ? 0 : 3 - (mensagem.length() % 4);
    quadro[quadro.length - 1] <<= espacosLivres * 8;
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaDeAplicacaoTransmissora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: mensagem = texto informado pelo usuario
  * Retorno: void
  *************************************************************** */
  public void CamadaDeAplicacaoTransmissora(String mensagem) {
    enviarParaCamadaFisica(mensagem);
  }
}
