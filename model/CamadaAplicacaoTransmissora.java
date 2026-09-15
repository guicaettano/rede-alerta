/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaAplicacaoTransmissora
* Funcao...........: Converter a mensagem em um quadro de bits
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaAplicacaoTransmissora {
  /* ***************************************************************
  * Metodo: CamadaDeAplicacaoTransmissora
  * Funcao: converter cada caractere da mensagem em 16 bits
  * Parametros: mensagem = texto informado pelo usuario
  * Retorno: void
  *************************************************************** */
  public void CamadaDeAplicacaoTransmissora(String mensagem) {
    int[] quadro = new int[mensagem.length() * 16];
    // Cada caractere Java ocupa 16 bits; cada posicao do vetor guarda um bit.
    for (int caractere = 0; caractere < mensagem.length(); caractere++) {
      for (int bit = 0; bit < 16; bit++) {
        quadro[caractere * 16 + bit] = (mensagem.charAt(caractere) >>> (15 - bit)) & 1;
      }
    }
    ControladorPrincipal.camadaFisicaTransmissora.CamadaFisicaTransmissora(quadro);
  }
}
