/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: TesteCamadas
* Funcao...........: Validar as conversoes iguais ao projeto de referencia
*************************************************************** */
package testes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import model.CamadaAplicacaoReceptora;
import model.CamadaAplicacaoTransmissora;
import model.CamadaFisicaReceptora;
import model.CamadaFisicaTransmissora;

public class TesteCamadas {
  /* ***************************************************************
  * Metodo: exigir
  * Funcao: interromper o teste quando uma condicao nao for atendida
  * Parametros: condicao = resultado logico que deve ser verdadeiro
  * Retorno: void
  *************************************************************** */
  private static void exigir(boolean condicao) {
    if (!condicao) throw new AssertionError("Resultado incorreto");
  }

  /* ***************************************************************
  * Metodo: invocar
  * Funcao: executar um metodo privado para testar a mesma implementacao
  * Parametros: tipo = classe, nome = metodo, argumento = valor recebido
  * Retorno: Object devolvido pelo metodo executado
  *************************************************************** */
  private static Object invocar(Class<?> tipo, String nome, Object argumento) {
    try {
      Method metodo = tipo.getDeclaredMethod(nome,
          argumento instanceof String ? String.class : int[].class);
      metodo.setAccessible(true);
      return metodo.invoke(null, argumento);
    } catch (Exception erro) {
      throw new AssertionError(erro);
    }
  }

  /* ***************************************************************
  * Metodo: removerPreenchimento
  * Funcao: retirar caracteres nulos usados para completar o ultimo int
  * Parametros: mensagem = texto reconstruido com preenchimento
  * Retorno: String sem os caracteres de preenchimento
  *************************************************************** */
  private static String removerPreenchimento(String mensagem) {
    int fim = mensagem.indexOf(0);
    return fim < 0 ? mensagem : mensagem.substring(0, fim);
  }

  /* ***************************************************************
  * Metodo: main
  * Funcao: executar os testes das tres codificacoes
  * Parametros: argumentos = argumentos recebidos pela linha de comando
  * Retorno: void
  *************************************************************** */
  public static void main(String[] argumentos) {
    CamadaFisicaTransmissora transmissor = new CamadaFisicaTransmissora();
    CamadaFisicaReceptora receptor = new CamadaFisicaReceptora();
    Random aleatorio = new Random(42);

    for (int caso = 0; caso < 1005; caso++) {
      String mensagem;
      if (caso == 0) mensagem = "A";
      else if (caso == 1) mensagem = "REDE ALERTA";
      else if (caso == 2) mensagem = "CAMADA FISICA";
      else if (caso == 3) mensagem = "01010101";
      else if (caso == 4) mensagem = "ALERTA";
      else {
        StringBuilder texto = new StringBuilder();
        for (int i = 1 + aleatorio.nextInt(180); i > 0; i--)
          texto.append((char) ('A' + aleatorio.nextInt(26)));
        mensagem = texto.toString();
      }

      int[] original = (int[]) invocar(
          CamadaAplicacaoTransmissora.class, "codificarEmArrayInt", mensagem);
      exigir(original.length == (int) Math.ceil(mensagem.length() / 4.0));

      int[][] fluxos = {
        transmissor.CamadaFisicaTransmissoraCodificacaoBinaria(original.clone()),
        transmissor.CamadaFisicaTransmissoraCodificacaoManchester(original.clone()),
        transmissor.CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(original.clone())
      };
      int[][] quadros = {
        receptor.CamadaFisicaReceptoraDecodificacaoBinaria(fluxos[0]),
        receptor.CamadaFisicaReceptoraDecodificacaoManchester(fluxos[1]),
        receptor.CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxos[2])
      };

      for (int[] quadro : quadros) {
        exigir(Arrays.equals(original, quadro));
        String recebida = (String) invocar(
            CamadaAplicacaoReceptora.class, "decodificarArrayInt", quadro.clone());
        exigir(mensagem.equals(removerPreenchimento(recebida)));
      }
    }

    int[] quadroConhecido = {(int) 0x80000000L};
    int[] binario = transmissor.CamadaFisicaTransmissoraCodificacaoBinaria(
        quadroConhecido.clone());
    exigir(binario.length == 32 && binario[0] == 1 && binario[1] == 0);

    int[] manchester = transmissor.CamadaFisicaTransmissoraCodificacaoManchester(
        quadroConhecido.clone());
    exigir(manchester.length == 64);
    exigir(Arrays.equals(Arrays.copyOf(manchester, 4), new int[] {1, 0, 0, 1}));

    int[] diferencial = transmissor.CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(
        new int[] {(int) 0xC0000000L});
    exigir(diferencial.length == 64);
    exigir(Arrays.equals(Arrays.copyOf(diferencial, 4), new int[] {1, 0, 0, 1}));

    System.out.println("OK: 3015 round-trips e vetores conhecidos.");
  }
}
