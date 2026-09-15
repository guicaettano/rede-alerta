/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaAplicacaoReceptora
* Funcao...........: Reconstruir a mensagem a partir do quadro de bits
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaAplicacaoReceptora {
  /* ***************************************************************
  * Metodo: CamadaDeAplicacaoReceptora
  * Funcao: agrupar os bits e reconstruir os caracteres da mensagem
  * Parametros: quadro = vetor com um bit em cada posicao
  * Retorno: void
  *************************************************************** */
  public void CamadaDeAplicacaoReceptora(int[] quadro) {
    if (quadro.length % 16 != 0) throw new IllegalArgumentException("Quadro incompleto.");
    StringBuilder mensagem = new StringBuilder();
    for (int inicio = 0; inicio < quadro.length; inicio += 16) {
      int caractere = 0;
      for (int bit = 0; bit < 16; bit++) {
        if (quadro[inicio + bit] != 0 && quadro[inicio + bit] != 1)
          throw new IllegalArgumentException("Bit invalido.");
        caractere = (caractere << 1) | quadro[inicio + bit];
      }
      mensagem.append((char) caractere);
    }
    ControladorPrincipal.aplicacaoReceptora.AplicacaoReceptora(mensagem.toString());
  }
}
